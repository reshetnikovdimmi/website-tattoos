# Код-ревью website-tattoos

Ревизия: ветка `feature/backup-scripts`, коммит `e906646` + незакоммиченная фича бэкапов на Яндекс.Диск.
Стек: Spring Boot 3.4.1, Java 17, Spring Security, Thymeleaf, JPA/Hibernate 6, MySQL 8 + Liquibase, Maven.
Объём: 168 Java-файлов (~8 000 строк) в `src/main`, 39 тестовых файлов, 18 Thymeleaf-шаблонов.

Как проверялось: чтение кода по слоям (security → контроллеры → сервисы → персистентность → конфиги), поиск
паттернов (`@ModelAttribute`, `catch (Exception)`, `findAll()`, `@Transactional`, `th:utext`, работа с файлами),
прогон `./mvnw test`, проверка `deploy.yml`/`backup.sh`, ревью незакоммиченной фичи бэкапов. Все находки
подтверждены чтением кода; спорные места (поведение `ModelMapper`, `@DataJpaTest`) проверены запуском.

## Сводка

| Критичность | Кол-во | Самое важное |
|---|---|---|
| Critical | 5 | Эскалация USER→ADMIN через `/user/**`, mass assignment на регистрации, stored XSS через `mapIframe`, AES-ключ в конфиге, открытый почтовый релей |
| High | 12 | CSRF off + удаление по GET, мутации без роли, битые проверки профиля, SQL/PII в prod-логах, лимит 1 МБ, drift схемы, plaintext SMTP, валидация файлов, транзакции, сломанная загрузка логотипа/тарифов, MySQL наружу, дефолтные креды |
| Medium | 14 | Обработка ошибок, авто-бэкап, синхронный бэкап, N+1, FK при удалении, верификация сайта, rate limit, Docker/заголовки |
| Low | 6 | Инъекция зависимостей, метод-безопасность, swagger, нейминг, конфиг-флаги |

---

## Critical

### C1. Любой зарегистрированный пользователь может стать ADMIN (`/user/**` вне `/admin`)
`UserController` смонтирован на `/user`, а проверка роли в `SecurityConfig` — только для `/admin/**`.
Наследуемый от `CRUDController` `POST /user/import` биндит **всю** сущность `User`, включая `role` и `password`,
а `AbstractCRUDService.create` сохраняет её через `repository.save()` без валидации и **без `PasswordEncoder`**.
Сценарий: обычный USER отправляет `role=ADMIN&password=<bcrypt-хеш своего пароля>` — строка сохраняется как есть,
`BCryptPasswordEncoder.matches` примет хеш при логине, и пользователь получает админку.
Плюс `GET /user/delete/{id}` без проверки роли удаляет любую учётку, включая администратора.

- [UserController.java:23](../src/main/java/ru/tattoo/maxsim/controller/admin/UserController.java#L23), [:44](../src/main/java/ru/tattoo/maxsim/controller/admin/UserController.java#L44)
- [SecurityConfig.java:30](../src/main/java/ru/tattoo/maxsim/config/SecurityConfig.java#L30)
- [CRUDController.java:55-62](../src/main/java/ru/tattoo/maxsim/controller/CRUDController.java#L55-L62)
- [AbstractCRUDService.java:33-35](../src/main/java/ru/tattoo/maxsim/service/impl/AbstractCRUDService.java#L33-L35)

**Фикс:** перенести на `/admin/user`; добавить `requestMatchers("/user/**").hasAuthority("ADMIN")`;
принимать DTO без `role`/`password`; пароль кодировать в сервисе; удаление — только POST/DELETE.

### C2. Регистрация позволяет перезаписать существующего пользователя (mass assignment)
`POST /process-registration` (публичный) биндит `@ModelAttribute("user") User`, включая `id`. `save()` при
непустом `id` делает `merge` → **UPDATE чужой строки**. Запрос с `id=1&login=evil&password=...` перезапишет
администратора: логин, email, хеш пароля. Владелец теряет доступ, строка `id=1` достаётся атакующему
(роль принудительно `USER`, но данные уничтожены).

- [RegistrationController.java:34](../src/main/java/ru/tattoo/maxsim/controller/auth/RegistrationController.java#L34)
- [AbstractCRUDService.java:33](../src/main/java/ru/tattoo/maxsim/service/impl/AbstractCRUDService.java#L33)

**Фикс:** отдельная форма `RegistrationForm` без `id/role/date/avatar` + маппинг в новый `User` (`id = null`);
страховка — `@InitBinder` с `setDisallowedFields("id", "role", "date", "avatar")`.

### C3. Stored XSS: `mapIframe` выводится через `th:utext`, а `/contact/import` доступен любому USER
`ContactController` (`/contact`) наследует `CRUDController`, поэтому `POST /contact/import` требует лишь
аутентификации, а не роли. Поле `ContactInfo.mapIframe` рендерится на главной без экранирования:
`<div th:utext="${current.mapIframe}">`. Любой зарегистрированный пользователь вставляет `<script>` —
он исполняется у всех посетителей и у администратора в его сессии: чтение SMTP-пароля из админки, вызовы
админских эндпоинтов.

- [ContactController.java:20](../src/main/java/ru/tattoo/maxsim/controller/client/ContactController.java#L20)
- [Index.html:601](../src/main/resources/templates/Index.html#L601)
- [ContactInfo.java:43](../src/main/java/ru/tattoo/maxsim/model/ContactInfo.java#L43)

**Фикс:** `/contact/import` — только ADMIN; `th:utext` заменить на `th:text` либо санитайзить
(OWASP Java HTML Sanitizer с allowlist для `iframe`).

### C4. AES-ключ шифрования токенов лежит в конфиге
`backup.encryption.key` записан в `application.properties` (изменение пока не закоммичено — при коммите уйдёт в
историю). Этим ключом `CryptoService` шифрует OAuth-токен Яндекс.Диска в БД, то есть шифрование не защищает
ни от утечки дампа БД, ни от утечки репозитория/логов/бэкапа.

- [application.properties:59](../src/main/resources/application.properties#L59)
- [CryptoService.java:28](../src/main/java/ru/tattoo/maxsim/service/CryptoService.java#L28)

**Фикс:** `backup.encryption.key=${BACKUP_ENCRYPTION_KEY:}`, в prod — падать при пустом значении; ключ и токен
Яндекс.Диска перевыпустить. Проверить, что ключ не попал в коммит.

**Статус: исправлено** — ключ вынесен в `BACKUP_ENCRYPTION_KEY` (`.env` → compose), деплой проверяет его наличие
до `docker pull`; в dev/test-профилях отдельные не-продакшн ключи. Остаётся перевыпустить старый ключ,
он остался в истории git и в локальном рабочем файле.

### C5. Открытый почтовый релей на публичном `/mail`
`/mail` в `permitAll`, а `EmailServiceImpl.createMailMessage` берёт получателя из формы (`details.getRecipient()`).
Любой отправляет письма с вашего SMTP-адреса на произвольные адреса — спам/фишинг от имени домена.

- [IndexController.java:49](../src/main/java/ru/tattoo/maxsim/controller/client/IndexController.java#L49)
- [EmailServiceImpl.java:86](../src/main/java/ru/tattoo/maxsim/service/impl/EmailServiceImpl.java#L86)

**Фикс:** игнорировать `recipient` и всегда слать на `settings.getRecipientEmail()`; rate limit/капча.

---

## High

### H1. CSRF отключён, а удаление сделано через GET
`.csrf(...disable)` + сессионная cookie = чужая страница может POST'ить в админку от имени администратора.
Дополнительно удаление через `GET /.../delete-section/{id}` и `GET /user/delete/{id}`: хватает `<img src=...>`
на стороннем сайте.

- [SecurityConfig.java:28](../src/main/java/ru/tattoo/maxsim/config/SecurityConfig.java#L28)
- [CRUDController.java:26-37](../src/main/java/ru/tattoo/maxsim/controller/CRUDController.java#L26-L37)

**Фикс:** включить CSRF (Thymeleaf сам подставит токен в `th:action`), удаления — POST/DELETE,
для AJAX — `$.ajaxSetup({headers: {'X-CSRF-TOKEN': ...}})`, cookie `SameSite=Lax/Strict`.

### H2. Наследуемые CRUD-мутации на `/contact/**` и `/reviews/**` доступны любому USER
Тот же механизм, что в C1/C3: `POST /contact/import`, `GET /contact/delete-section/{id}`,
`POST /reviews/import` (с произвольным `userName` — импersonation), `GET /reviews/delete-section/{id}`.

- [ContactController.java:20](../src/main/java/ru/tattoo/maxsim/controller/client/ContactController.java#L20)
- [ReviewsController.java:24-27](../src/main/java/ru/tattoo/maxsim/controller/client/ReviewsController.java#L24-L27)

**Фикс:** `@PreAuthorize("hasAuthority('ADMIN')")` на изменяющих методах или не наследовать `CRUDController`
публичными контроллерами; `userName` брать из `Principal`.

### H3. Обновление профиля: инвертированные проверки уникальности
Если логин **не изменился** — «уже существует»; если изменился — проверки занятости нет и значение пишется.
Аналогично email. Итог: профиль нельзя сохранить, не меняя оба поля, а смена на чужой логин падает на
unique-constraint (500).

- [UserServiceImpl.java:109-126](../src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java#L109-L126)

**Фикс:** `if (!newLogin.equals(current) && userRepository.existsByLogin(newLogin)) throw ...;` и то же для email;
проверку и сохранение — в одной транзакции.

### H4. Смена логина ломает сессию и роняет запрос
Логин — часть FK (`images.user_name`, `reviews_user.user_name`, `ON UPDATE CASCADE`), но после
`updateUserProfile` контроллер снова ищет пользователя по **старому** `principal.getName()`; `findByLogin`
возвращает `null`, а `modelMapper.map(null, UserDTO.class)` кидает `IllegalArgumentException: source cannot be null`
(проверено запуском). Итог — 500 после успешного сохранения, а обработчик ошибки сам падает (см. M6).

- [LkController.java:169-172](../src/main/java/ru/tattoo/maxsim/controller/client/LkController.java#L169-L172)
- [UserServiceImpl.java:96-98](../src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java#L96-L98)

**Фикс:** обновлять `SecurityContext` после смены логина (или запретить смену), `findByLogin` — `orElseThrow`,
`modelMapper.map` не вызывать для `null`.

### H5. В production логируются SQL и все bind-параметры
База включает `org.hibernate.SQL=DEBUG` и `BasicBinder=TRACE`, `application-prod.properties` их не отменяет —
в логи попадают SQL и значения (email, bcrypt-хеши, SMTP-настройки, зашифрованный токен). Дополнительно
`CRUDController` логирует сущность целиком, а `User` помечен `@ToString` — пароль оказывается в строке лога.

- [application.properties:37-38](../src/main/resources/application.properties#L37-L38)
- [application-prod.properties:8](../src/main/resources/application-prod.properties#L8)
- [CRUDController.java:60](../src/main/java/ru/tattoo/maxsim/controller/CRUDController.java#L60)
- [User.java:17](../src/main/java/ru/tattoo/maxsim/model/User.java#L17)

**Фикс:** в prod `org.hibernate.SQL=WARN`, `BasicBinder=OFF`; debug-настройки — в dev-профиль;
`@ToString.Exclude` на `password`; не логировать сущности целиком.

### H6. Лимит загрузки 1 МБ, заявленные 10 МБ не работают
`spring.servlet.multipart.max-file-size` не задан → дефолт Spring Boot **1 МБ** на файл.
`app.storage.max-file-size`, `upload.max-file-size` и `StorageProperties.getMaxFileSize()` не используются,
а текст ошибки обещает 5 МБ. Фото с телефона (3–8 МБ) падают.

- [application.properties:53](../src/main/resources/application.properties#L53)
- [StorageProperties.java:20](../src/main/java/ru/tattoo/maxsim/config/StorageProperties.java#L20)
- [GlobalExceptionHandler.java:66](../src/main/java/ru/tattoo/maxsim/exceptions/GlobalExceptionHandler.java#L66)

**Фикс:** `spring.servlet.multipart.max-file-size=${APP_STORAGE_MAX_FILE_SIZE:10MB}`,
`max-request-size=12MB`; размер проверять через `StorageProperties`; текст ошибки — из конфига.

### H7. Схема управляется двумя инструментами, и они расходятся
`ddl-auto=update` работает одновременно с Liquibase. Уже есть конкретный drift: в `db.changelog-master.yaml`
не подключён существующий `create-review-section-table.yaml`; для сущности `Commits` changeset'а нет вовсе;
в `create-features-section-table.yaml` колонка `iconClass`, а сущность ждёт `icon_class` — Hibernate создаёт
вторую колонку, и расхождение маскируется. Плюс смешанные стратегии ID: у 10 сущностей
`GenerationType.TABLE`, хотя Liquibase создаёт `autoIncrement` и сидит строки с id 1..7 (`setting_website`,
`contact_info`, `home`). На чистой БД первый Hibernate-insert для такой сущности начнёт с id=1 → duplicate key.

- [application.properties:15](../src/main/resources/application.properties#L15)
- [db.changelog-master.yaml](../src/main/resources/db/changelog/db.changelog-master.yaml)
- [create-features-section-table.yaml:29](../src/main/resources/db/changelog/changeset/create-features-section-table.yaml#L29)
- [SettingWebsite.java:17](../src/main/java/ru/tattoo/maxsim/model/SettingWebsite.java#L17)

**Фикс:** подключить review-section и `commits`, исправить `iconClass` → `icon_class`, унифицировать ID на
`IDENTITY` (или на sequence везде), `ddl-auto=validate` в prod; прогон миграций на чистой БД в CI.

### H8. SMTP-пароль хранится в открытом виде и рендерится в HTML
`saveSettings` сохраняет пароль как есть; форма `th:field="*{password}"` подставляет его в `value` — пароль виден
в исходнике страницы, истории браузера и DOM.

- [MailSettingsServiceImpl.java:53](../src/main/java/ru/tattoo/maxsim/service/impl/MailSettingsServiceImpl.java#L53)
- [fragment-admin.html:3235](../src/main/resources/templates/fragment-admin.html#L3235)

**Фикс:** шифровать через `CryptoService`, в форму отдавать пустое поле («оставьте пустым, чтобы не менять»).

### H9. Валидация загрузок обходится, аватары не проверяются вообще
Проверка — только `contentType.startsWith("image/")`, причём `null` проходит; аватар сохраняется без проверок.
Файл кладётся в `/images/**` с оригинальным расширением и отдаётся как `text/html` при прямом запросе:
загрузка `x.html` с `Content-Type: image/png` даёт same-origin XSS. Объявленный `StorageProperties.allowedTypes`
не используется.

- [AbstractCRUDService.java:200-204](../src/main/java/ru/tattoo/maxsim/service/impl/AbstractCRUDService.java#L200-L204)
- [UserServiceImpl.java:78](../src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java#L78)
- [FileSystemImageStorage.java:75](../src/main/java/ru/tattoo/maxsim/storage/Impl/FileSystemImageStorage.java#L75)

**Фикс:** allowlist расширений + magic bytes (`ImageIO.read`), безопасное серверное имя, перекодирование картинки,
`Content-Disposition: attachment` для не-изображений.

### H10. Нет транзакций вокруг «файл + БД», порядок удаления опасен
`saveImg` пишет файл, потом сущность (ошибка БД → файл-сирота); `deleteById` удаляет файл **до** строки
(ошибка БД → строка без картинки). `@Transactional` в CRUD-сервисах нет, `@Version` нет — lost update на
read-modify-write (например, флаг «лучшее»).

- [AbstractCRUDService.java:87-99](../src/main/java/ru/tattoo/maxsim/service/impl/AbstractCRUDService.java#L87-L99), [:156-179](../src/main/java/ru/tattoo/maxsim/service/impl/AbstractCRUDService.java#L156-L179)

**Фикс:** `@Transactional` на изменяющих методах; файл создавать/удалять после коммита
(`TransactionSynchronization`/`@TransactionalEventListener`) с компенсацией; `@Version` на изменяемых сущностях.

### H11. Загрузка логотипа/хлебных крошек и правка тарифов падают
`SettingWebsiteServiceImpl.prepareObject` делает `findById(entity.getId())` и `savedEntity.get()`, а формы
`/admin/setting/logo-import` и `/breadcrumb-import` передают только `section` (без `id`) → `findById(null)`.
Аналогично `PriceSectionServiceImpl.create` (`findById(null).get()`): форма `/admin/price/import` не передаёт `id`.
Файл при этом уже записан в хранилище — остаётся сирота.

- [SettingWebsiteServiceImpl.java:54-60](../src/main/java/ru/tattoo/maxsim/service/impl/SettingWebsiteServiceImpl.java#L54-L60)
- [PriceSectionServiceImpl.java:51-58](../src/main/java/ru/tattoo/maxsim/service/impl/PriceSectionServiceImpl.java#L51-L58)

**Фикс:** искать запись по бизнес-ключу (`section`) или явно обрабатывать `id == null` (insert);
`Optional.orElseThrow` с понятной ошибкой; файл сохранять после успешного определения сущности.

### H12. Порт MySQL опубликован наружу и дефолтные учётки в репозитории
`docker-compose.yml` пробрасывает `3307:3306` на все интерфейсы (пользователь БД — с хоста `%`).
Плюс в репозитории: миграция создаёт пользователя `ADMIN` с bcrypt-хешем, а нагрузочный тест
`load_backup/ImageUploadLoadTest.java` содержит `username=ADMIN`, `password=admin` (файл в git).

- [docker-compose.yml:36](../docker-compose.yml#L36)
- [create-user-table.yaml:37-49](../src/main/resources/db/changelog/changeset/create-user-table.yaml#L37-L49)
- [ImageUploadLoadTest.java:33-34](../load_backup/ImageUploadLoadTest.java#L33-L34)

**Фикс:** `127.0.0.1:3307:3306` (или убрать порт), сменить боевой пароль администратора, убрать креды из
репозитория (env), не создавать админа миграцией.

---

## Medium

### M1. Обработчик исключений отдаёт детали наружу и ломает AJAX
`@ExceptionHandler(Exception.class)` кладёт `ex.getMessage()` в модель, шаблон `error.html` его выводит
(SQL, пути, внутренние сообщения). Тот же метод возвращает HTML-страницу фрагментным/JSON-запросам —
JS получает HTML вместо JSON.

- [GlobalExceptionHandler.java:21-27](../src/main/java/ru/tattoo/maxsim/exceptions/GlobalExceptionHandler.java#L21-L27)
- [error.html:17](../src/main/resources/templates/error.html#L17)

**Фикс:** детали только в лог + correlation id; разделить `@RestControllerAdvice` и MVC; для фрагментов —
`modal::modal-body` с нейтральным текстом.

### M2. Чекбокс «автоматический бэкап» ничего не включает
Есть `enabled`, `lastBackupAt`, `lastBackupStatus`, но `@Scheduled`/`@EnableScheduling` в проекте нет.
Автозапуск существует только в `scripts/backup.sh` (cron), о чём UI не сообщает.

- [BackupSettings.java:27](../src/main/java/ru/tattoo/maxsim/model/BackupSettings.java#L27)

**Фикс:** `@Scheduled` с проверкой `enabled` и блокировкой от наложения, либо убрать чекбокс и написать про cron.

**Статус: исправлено** — добавлены `BackupScheduler` (`app.backup.cron`, по умолчанию 3:00), `@EnableScheduling`
и блокировка в `BackupService`/`BackupRunRegistry`; чекбокс `enabled` теперь реально управляет автозапуском.

### M3. Создание бэкапа синхронно в HTTP-запросе
Дамп + архив + загрузка идут в `POST /admin/backup/create`; при большом архиве — таймаут прокси, двойной клик
запускает второй дамп.

- [BackupSettingsController.java:70](../src/main/java/ru/tattoo/maxsim/controller/admin/BackupSettingsController.java#L70)

**Фикс:** `@Async` + статус/polling, либо `tryLock` и `202 Accepted`.

**Статус: исправлено** — `POST /admin/backup/create` запускает `BackupWorker` в отдельном потоке и сразу
возвращает ответ, UI опрашивает `GET /admin/backup/status`; добавлена ротация на Диске (`app.backup.keep`).

### M4. Полные выборки, EAGER-коллекции и логирование на каждый элемент
`AdminController` грузит `findAll()` по 9 таблицам; `Home.price` — `EAGER`, остальные коллекции `LAZY` при
включённом OSIV дают отдельные SELECT'ы из шаблонов; галерея логирует каждое изображение на INFO.

- [AdminController.java:64-72](../src/main/java/ru/tattoo/maxsim/controller/admin/AdminController.java#L64-L72)
- [Home.java:36-38](../src/main/java/ru/tattoo/maxsim/model/Home.java#L36-L38)
- [ImagesServiceImpl.java:89-114](../src/main/java/ru/tattoo/maxsim/service/impl/ImagesServiceImpl.java#L89-L114)

**Фикс:** `Pageable` для админки, DTO/проекции, `@EntityGraph` там, где нужны связи, `log.debug`.

### M5. Удаление пользователя, FK и уникальность email
FK `images.user_name → user(login) ON DELETE RESTRICT`: удаление пользователя с работами/отзывами падает,
а аватар уже удалён. `UserValidator` проверяет только логин — регистрация с занятым email даёт 500.

- [update-foreign-keys.yaml:40-50](../src/main/resources/db/changelog/changeset/update-foreign-keys.yaml#L40-L50)
- [UserValidator.java:31](../src/main/java/ru/tattoo/maxsim/validator/UserValidator.java#L31)

**Фикс:** soft-delete или `ON DELETE CASCADE`/проверка с понятной ошибкой; добавить проверку email;
файл удалять после БД.

### M6. Блок обработки ошибок в `LkController` сам падает с NPE
`e.getMessage()+": "+e.getCause().getMessage() == null ? ... : ...` — приоритет операторов делает условие
всегда ложным, а `getCause()` может быть `null` → NPE в обработчике; в модалку уходит `e.toString()`.

- [LkController.java:188](../src/main/java/ru/tattoo/maxsim/controller/client/LkController.java#L188)

**Фикс:** `log.error("...", e); model.addAttribute("error", "Не удалось обновить профиль");`.

### M7. Верификация сайта: файлы не отдаются, тумблер не выключается
`/yandex_{code}.html`, `/google{code}.html` и `/{filename:.+\.html}` не входят в `permitAll` → краулер получает
редирект на `/login`, верификация не проходит. `SiteVerificationServiceImpl.save` принудительно ставит
`active = true`, поэтому выключение через toggle не работает; уникального индекса на `(engine, method, active)`
нет — при гонке возможны две активные записи и `IncorrectResultSizeDataAccessException` в `Optional`-поиске.

- [VerificationFileController.java:28-77](../src/main/java/ru/tattoo/maxsim/controller/VerificationFileController.java#L28-L77)
- [SiteVerificationServiceImpl.java:55](../src/main/java/ru/tattoo/maxsim/service/impl/SiteVerificationServiceImpl.java#L55)

**Фикс:** точечный `permitAll` для `/*.html`-паттернов верификации; не перетирать `active` в `save()`;
уникальный индекс/блокировка.

### M8. Массовое присвоение в админском CRUD
Наследники `CRUDController` биндят сущность целиком, включая `id`: подстановка чужого `id` в форму перезапишет
другую строку. Для админа риск ниже, но это тот же класс ошибки, что C1/C2.

- [CRUDController.java:44-64](../src/main/java/ru/tattoo/maxsim/controller/CRUDController.java#L44-L64)

**Фикс:** DTO-формы либо `@InitBinder` с `setAllowedFields`.

### M9. Нет ограничений частоты на логин, регистрацию и почту
Brute-force логина, массовая регистрация, спам через форму — без лимитов.

**Фикс:** Bucket4j/cache-счётчик по IP+логину, капча на публичные формы.

### M10. Docker: контейнер от root, без лимитов
В рантайм-стадии нет `USER`; при RCE атакующий получает root в контейнере.

- [Dockerfile:16-35](../Dockerfile#L16-L35)

**Фикс:** непривилегированный пользователь, `chown` каталога загрузок, `USER app`, лимиты ресурсов в compose.

### M11. Профиль по умолчанию — `dev` со стектрейсами
`spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}`, а `application-dev.properties` включает
`server.error.include-stacktrace=always` и `include-message=always`. Запуск без переменной окружения отдаёт
стектрейсы клиенту.

- [application.properties:6](../src/main/resources/application.properties#L6)
- [application-dev.properties:16-17](../src/main/resources/application-dev.properties#L16-L17)

**Фикс:** дефолт — `prod`, stacktrace в dev — `on_param`/никогда.

### M12. Нет CSP/HSTS/Referrer-Policy, cookie без `Secure`
`headers` настраивает только `frameOptions`; `server.servlet.session.cookie.*` не задан.

- [SecurityConfig.java:70-74](../src/main/java/ru/tattoo/maxsim/config/SecurityConfig.java#L70-L74)

**Фикс:** CSP (с nonce для inline-скриптов), HSTS, Referrer-Policy, `cookie.secure=true`, `SameSite`.

### M13. Catch-all `*.html` в контроллере верификации
Маршрут `/{filename:.+\.html}` перехватывает любые HTML-пути в корне, а `contains(code)` — слабое сопоставление;
контент отдаётся как `text/html` из админских данных.

- [VerificationFileController.java:62-77](../src/main/java/ru/tattoo/maxsim/controller/VerificationFileController.java#L62-L77)

**Фикс:** ограничить шаблон имени (`yandex_[A-Za-z0-9]+\.html`), не отдавать произвольный HTML.

### M14. Swagger/OpenAPI открыт публично
`/swagger-ui/**`, `/v3/api-docs/**` в `permitAll` — карта эндпоинтов, включая «скрытые» `/user/**`.

- [SecurityConfig.java:45-46](../src/main/java/ru/tattoo/maxsim/config/SecurityConfig.java#L45-L46)

**Фикс:** закрыть ADMIN или отключить springdoc в prod (`springdoc.api-docs.enabled=false`).

---

## Low

- **L1.** Aspect `execution(* ru.tattoo.maxsim.service.*.*(..))` не покрывает `service.impl`, а при исключении
  метрика не пишется ([PerformanceLoggerAspect.java:17](../src/main/java/ru/tattoo/maxsim/config/aspect/PerformanceLoggerAspect.java#L17)).
- **L2.** `@EnableMethodSecurity` есть, `@PreAuthorize`/`@Secured` — ни одного; защита только по URL (что и дало C1/H2).
- **L3.** 94 поля с `@Autowired` против 13 конструкторных; нейминг пакетов `storage/Impl`, `Impl.FileSystemImageStorage`.
- **L4.** `spring.main.allow-bean-definition-overriding=true` маскирует случайные дубли бинов.
- **L5.** `SitemapController` использует `e.printStackTrace()` ([SitemapController.java:24](../src/main/java/ru/tattoo/maxsim/controller/SitemapController.java#L24));
  `/api/sitemap` отдаёт JAXB-модель как JSON без необходимости.
- **L6.** `PageSize.getLisPageSize()` возвращает изменяемый статический список; `AbstractCRUDService.findAll()`
  делает `(List) findAll()` + `reverse()` вместо `Sort` в запросе; `Images.id` — `GenerationType.TABLE`
  (см. H7).

---

## Тесты и процесс

- 182 тестовых метода в 39 файлах, но surefire исключает `UI/**`, поэтому выполняется 86 тестов.
- Текущее состояние: **25 failures + 12 errors** и на рабочей ветке, и на чистом `main` (`d3cd9aa`) — суита
  красная давно; CI этого не видит, потому что `deploy.yml` не запускает тесты (Dockerfile собирает с `-DskipTests`).
- Часть падений — общая in-memory БД: `jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1` + `create-drop` в разных
  Spring-контекстах; один контекст при закрытии дропает таблицы, другой видит «database is empty».
  ([application-test.properties:6](../src/main/resources/application-test.properties#L6))
- `SecurityTest`/`FileUploadSecurityTest` ожидают 200/`admin?error=*`, получают 302/500 — писались под другое
  поведение security.
- Три тестовых файла используют assert'ы TestNG внутри JUnit 5; тестов на права (`/user/**`, `/contact/import`)
  и на multipart-лимиты нет.

**Фикс процесса:** уникальная БД на контекст (`generate-unique-name=true` или `${random.uuid}`), зелёный
минимум (unit + security) как гейт в CI, Selenium — в отдельный модуль/профиль Failsafe.

## Что уже сделано хорошо

- Единый источник пути хранения (`StorageProperties`) и защита от path traversal в `FileSystemImageStorage`
  (`normalize()` + `startsWith`), UUID-префикс имени.
- BCrypt для паролей, AES-GCM со случайным IV и валидацией длины ключа в `CryptoService`.
- Секреты БД/почты вынесены в `.env` (`.gitignore`), `.env.example` без значений.
- SQL-инъекций нет: все `@Query` статические с именованными параметрами.
- IDOR в личном кабинете закрыт: владелец берётся из `Principal`, а не из запроса.
- Liquibase-миграции на все основные таблицы, FKs с `ON UPDATE CASCADE`.
- CI/CD: health-check после деплоя, откат на предыдущий образ, тег по SHA, `concurrency`.
- `scripts/backup.sh`: `set -euo pipefail`, sha256, ротация, offsite-загрузка на Диск, понятные ошибки.
- Юнит-тесты на хранилище/конфиг и новые тесты бэкапов (Yandex upload, tar, dump).

## План по приоритетам

1. **Сегодня:** C1 (`/user/**` → `/admin` + DTO без role/password), C2 (DTO регистрации), C3 (`/contact/import`
   только ADMIN + `th:text`), C5 (получатель из настроек), C4 (ключ в env + перевыпуск), H1 (CSRF + POST-удаления).
2. **На этой неделе:** H2, H3, H4, H5 (логи prod), H6 (multipart), H11 (логотип/тарифы), H12 (порт/креды).
3. **Следующая итерация:** H7 (drift + ID-стратегии + `validate`), H8 (шифрование SMTP), H9 (magic bytes),
   H10 (транзакции), M1-M3, M7 (верификация), M11-M12 (заголовки).
4. **Постоянно:** зелёная суита + тестовый гейт в CI, `@PreAuthorize` на изменяющих методах, DTO вместо сущностей
   в MVC, `@ToString.Exclude` для секретов.
