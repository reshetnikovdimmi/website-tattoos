# Бэкапы для новичка: стек вызовов и что делает каждый класс

Эта страница — «экскурсия по коду» для того, кто только учит Java и Spring. Она объясняет,
что происходит после нажатия кнопки «Создать бэкап», кто кого вызывает и зачем нужен каждый класс.
Подробная инструкция для эксплуатации — в [backup.md](backup.md).

## 0. Минимум теории, без которого код будет непонятен

| Понятие | Простыми словами | Где в коде |
|---|---|---|
| **Бин (bean)** | Объект, которым управляет Spring: он создаёт его один раз и передаёт тем, кому нужно | любой класс с `@Service`, `@Component`, `@Controller` |
| **Внедрение зависимостей (DI)** | Класс не создаёт зависимости через `new`, а получает их в конструкторе | `@RequiredArgsConstructor` + `private final ...` |
| **Контроллер** | Принимает HTTP-запрос и возвращает ответ (HTML или JSON) | `BackupSettingsController` |
| **Сервис** | Бизнес-логика: то, что не про HTTP | `BackupService`, `RestoreService` |
| **DTO / record** | Небольшой объект «только данные», чтобы не тащить сущность БД в шаблон | `BackupResult`, `BackupSet` |
| **`@Value`** | Подставляет значение из `application.properties` или переменной окружения | `app.backup.keep` |
| **`@Async`** | Метод выполняется в отдельном потоке, вызов возвращается сразу | `BackupWorker.run()` |
| **`@Scheduled`** | Spring сам вызывает метод по расписанию (cron) | `BackupScheduler.scheduledBackup()` |
| **`@Transactional`** | Несколько действий с БД выполняются как одно целое: всё или ничего | `BackupSettingsService.save()` |
| **checked / unchecked исключения** | `IOException` компилятор требует обработать; `RuntimeException` — нет | `catch (IOException)` в `BackupService` |
| **`try-with-resources`** | Автоматически закрывает файлы/потоки даже при ошибке | `archiveImages()`, `restoreImages()` |

## 1. Общая картина

```
 Браузер (админка)
   │  fetch / $.ajax
   ▼
 BackupSettingsController            ← HTTP-слой: принять запрос, вернуть JSON/HTML
   │
   ├── BackupJobService              ← «можно ли начать?» (занимает слот)
   │      ├── BackupRunRegistry      ← состояние в памяти: IDLE/RUNNING/SUCCESS/FAILED
   │      └── BackupWorker (@Async)  ← отдельный поток, чтобы запрос не висел
   │             └── BackupService   ← сердце: дамп → архив → загрузка → ротация
   │                    ├── BackupSettingsService  ← настройки и токен из БД
   │                    ├── CryptoService          ← расшифровать токен (AES-GCM)
   │                    ├── ProcessBuilder(mysqldump) ← внешняя программа, дамп БД
   │                    ├── YandexDiskService      ← REST API Диска (upload/download)
   │                    └── BackupSet              ← сгруппировать файлы для UI
   │
   └── BackupScheduler (@Scheduled)  ← раз в сутки сам вызывает BackupJobService

 Браузер периодически спрашивает: GET /admin/backup/status
   └── BackupRunRegistry.current()   ← просто прочитать состояние и отдать JSON
```

Восстановление устроено зеркально:

```
BackupSettingsController.restore()
  → RestoreJobService → RestoreRunRegistry + RestoreWorker (@Async)
      → RestoreService: скачать с Диска → mysql (БД) → распаковать картинки
```

## 2. Кто кого вызывает

| Класс | Роль | Кого вызывает | Кто вызывает его |
|---|---|---|---|
| `BackupSettingsController` | HTTP | `BackupSettingsService`, `BackupJobService`, `RestoreJobService` | браузер (AJAX) |
| `BackupJobService` | «фасад запуска» | `BackupRunRegistry`, `BackupWorker` | контроллер, планировщик |
| `BackupRunRegistry` | память о статусе | — | `BackupJobService`, `BackupWorker`, контроллер |
| `BackupWorker` | фоновая обёртка | `BackupService` | `BackupJobService` |
| `BackupService` | сам бэкап | `BackupSettingsService`, `CryptoService`, `YandexDiskService`, `BackupOperationLock`, `mysqldump` | `BackupWorker` |
| `BackupScheduler` | расписание | `BackupSettingsService`, `BackupJobService` | Spring (по cron) |
| `BackupSettingsService` | настройки + статус в БД | `BackupSettingsRepository`, `CryptoService`, `YandexDiskService` | `BackupService`, контроллер |
| `YandexDiskService` | REST-клиент Диска | `RestTemplate` | `BackupService`, `RestoreService`, `BackupSettingsService` |
| `BackupOperationLock` | «один за раз» | `Semaphore` | `BackupService`, `RestoreService` |
| `BackupSet` | группировка файлов | — | `BackupService` (ротация), контроллер (UI) |
| `RestoreService` | восстановление | те же сервисы + `mysql`, `tar` | `RestoreWorker` |

## 3. Стек вызовов: «Создать бэкап»

### 3.1. Что делает браузер

`main.js` → `createBackup()`:

1. `POST /admin/backup/create`;
2. если сервер ответил `success: true` — начинает опрос `GET /admin/backup/status` каждые 3 секунды;
3. когда статус перестал быть `RUNNING` — показывает результат и перезагружает список копий.

### 3.2. Что происходит на сервере, шаг за шагом

```
BackupSettingsController.create()                    HTTP-поток Tomcat
  ├─ if (restoreJobService.isRunning()) → ошибка «идёт восстановление»
  └─ BackupJobService.start()
       ├─ BackupRunRegistry.start()                  ставит RUNNING; если уже RUNNING → false
       └─ BackupWorker.run()                         @Async: работа уходит в поток backup-1,
            │                                        а метод сразу возвращается
            ▼
       BackupWorker.run()                            поток backup-1
         ├─ BackupService.createBackup()
         │    ├─ BackupOperationLock.tryLock()       пропуск «только одна операция»
         │    ├─ BackupService.doCreateBackup()
         │    │    ├─ settingsService.getOrCreate()          настройки из БД
         │    │    ├─ cryptoService.decrypt(token)           расшифровать OAuth-токен
         │    │    ├─ Files.createTempDirectory(...)         временная папка
         │    │    ├─ dumpDatabase(...)                      mysqldump → db.sql → db.sql.gz
         │    │    ├─ archiveImages(...)                     папка картинок → images.tar.gz
         │    │    ├─ yandexDiskService.createFolder(...)    папка на Диске (409 = уже есть)
         │    │    ├─ yandexDiskService.uploadFile(...)      GET href → PUT файла (2 шага)
         │    │    ├─ rotateRemoteBackups(...)               оставить N последних копий
         │    │    └─ settingsService.markBackupSuccess()    статус SUCCESS в БД
         │    ├─ finally: operationLock.unlock()             вернуть пропуск
         │    └─ finally: deleteRecursively(tempDir)         удалить временные файлы
         ├─ registry.success(dir)                    статус SUCCESS для UI
         └─ catch (Exception e) → registry.failed(...) + лог
```

Возврат управления: `BackupJobService.start()` возвращает `true` ещё до того, как дамп начался,
контроллер сразу отвечает браузеру `{success: true}`.

### 3.3. Что делает `dumpDatabase()` внутри

1. Разбирает `spring.datasource.url` регуляркой: `jdbc:mysql://db:3306/tattoo` → host, port, database.
2. Собирает список аргументов: `mysqldump --host=... --port=... --user=... --single-transaction ... tattoo`.
3. `ProcessBuilder` запускает **внешнюю программу** (Java не умеет сама делать SQL-дамп).
4. Пароль передаётся через переменную окружения `MYSQL_PWD` — иначе его было бы видно в `ps`.
5. `waitFor(15, MINUTES)` — ждём с таймаутом, чтобы не зависнуть навсегда.
6. Проверяем код возврата и размер файла; пустой дамп — ошибка.
7. `gzip()` сжимает `db.sql` в `db.sql.gz` потоково (файл не грузится в память).

### 3.4. Что делает `archiveImages()` внутри

`Files.walk(папка)` обходит все файлы и подпапки; каждый файл добавляется в `tar`-архив с именем
`images/<относительный путь>`. Потоки вложены: `tar` → `GZIPOutputStream` → файл.
`try-with-resources` закроет их в обратном порядке, даже если случится исключение.

## 4. Стек вызовов: планировщик

```
Spring (cron 0 0 3 * * *)                  поток планировщика
  └─ BackupScheduler.scheduledBackup()
       ├─ settingsService.getOrCreate()
       ├─ если enabled == false        → лог и выход
       ├─ если токен пустой           → лог и выход
       └─ BackupJobService.start()    → дальше всё как в разделе 3.2
```

Планировщик **не делает бэкап сам** — он только решает «пора/не пора» и передаёт работу тому же
фасаду, что и кнопка. Поэтому логика не дублируется.

## 5. Стек вызовов: cron-скрипт (вне Java)

`scripts/backup.sh` не использует Java вообще. Он:

1. `docker exec db mysqldump ... | gzip > db.sql.gz`;
2. `tar -czf images.tar.gz ...`;
3. копирует compose/конфиг и считает `sha256sum`;
4. удаляет старые локальные копии;
5. загружает файлы на Диск теми же двумя HTTP-запросами (`GET upload` → `PUT`), что и Java.

Имена файлов совпадают (`db_<дата>.sql.gz`, `images_<дата>.tar.gz`), поэтому админка видит и
Java-копии, и cron-копии, а ротация работает для всех.

## 6. Стек вызовов: «Восстановить»

```
backup.js restoreBackup(stamp)
  └─ prompt «Введите ВОССТАНОВИТЬ»
       └─ POST /admin/backup/restore {stamp, confirmation: RESTORE}
            └─ BackupSettingsController.restore()
                 ├─ проверка confirmation
                 ├─ проверка, что бэкап не идёт
                 └─ RestoreJobService.start(stamp)
                      ├─ RestoreRunRegistry.start(stamp)   RUNNING
                      └─ RestoreWorker.run(stamp)          @Async
                           └─ RestoreService.restore(stamp)
                                ├─ BackupOperationLock.tryLock()
                                ├─ settingsService.getOrCreate() + decrypt token
                                ├─ BackupSet.from(listFiles)  найти нужную дату
                                ├─ downloadFile(db.sql.gz)     на сервер во временную папку
                                ├─ restoreDatabase(...)       gzip → restore.sql → mysql (stdin)
                                ├─ downloadFile(images.tar.gz)
                                ├─ restoreImages(...)         tar → каталог загрузок
                                └─ finally: unlock + удалить временные файлы
```

Браузер всё это время опрашивает `GET /admin/backup/restore/status`.

## 7. Что происходит при ошибке

| Где ошибка | Что видит пользователь | Что происходит в коде |
|---|---|---|
| нет токена | «OAuth-токен не задан» | `BackupException` → `BackupWorker` ловит → `FAILED` |
| `mysqldump` не найден | подсказка про установку клиента | `ProcessBuilder.start()` бросает `IOException` → `BackupException` |
| `mysqldump` вернул код ≠ 0 | текст из stderr + подсказка | проверка `process.exitValue()` |
| Диск недоступен | сообщение API | `YandexDiskException` (наследник `RuntimeException`) |
| ошибка записи на диск | «Ошибка ввода-вывода...» | `catch (IOException)` в `doCreateBackup` |
| ошибка при restore | сообщение в `restore/status` | `RestoreService` бросает `BackupException` |

Общий принцип: **исключение не теряется**. Оно либо превращается в понятное сообщение, либо
логируется, а статус в реестре/БД становится `FAILED`.

## 8. Где смотреть состояние

- **В памяти** (быстро, но сбрасывается при рестарте): `BackupRunRegistry`, `RestoreRunRegistry`.
- **В БД** (переживает рестарт): `backup_settings.last_backup_at`, `last_backup_status`.
- **В файлах на Диске**: `BackupSet.from(...)` группирует их в карточки для UI.
- **В логах**: `BackupWorker`, `RestoreWorker`, `BackupService`, `YandexDiskService`.

## 9. Как отлаживать

Поставьте breakpoint (точку останова) и нажмите «Создать бэкап»:

| Файл | Метод | Что смотреть |
|---|---|---|
| `BackupSettingsController` | `create()` | пришёл ли запрос, что вернул `start()` |
| `BackupJobService` | `start()` | `registry.start()` → true/false |
| `BackupWorker` | `run()` | в каком потоке (`backup-1`), какое исключение |
| `BackupService` | `doCreateBackup()` | значения `stamp`, `diskDir`, `dbName` |
| `BackupService` | `dumpDatabase()` | аргументы `mysqldump`, stderr, код возврата |
| `BackupService` | `rotateRemoteBackups()` | какие копии попали в `keepStamps` |
| `YandexDiskService` | `uploadFile()` | `href`, размер файла |

Полезно: включить SQL-логи и смотреть, как меняется `backup_settings` после прогона.

Запуск только бэкап-тестов (в проекте `skipTests=true`, поэтому явно включаем):

```bash
./mvnw -DskipTests=false -Dtest='Backup*Test,Restore*Test,AdminBackupTemplateTest' test
```

## 10. В каком порядке читать код

1. **`BackupSettingsController`** — какие действия доступны снаружи.
2. **`BackupJobService` + `BackupRunRegistry`** — как не запустить два бэкапа и как хранится статус.
3. **`BackupWorker`** — почему `@Async` вынесен в отдельный класс.
4. **`BackupService.doCreateBackup()`** — главный сценарий по шагам.
5. **`BackupService.dumpDatabase()` и `archiveImages()`** — работа с процессами и файлами.
6. **`YandexDiskService.uploadFile()`** — пример REST-клиента и двухшаговой загрузки.
7. **`BackupSet`** — преобразование «плоских» данных API в удобные объекты.
8. **`RestoreService`** — обратный сценарий и защита от path traversal.
9. **`BackupSettings`** (сущность) — как настройки лежат в БД.
10. **`CryptoService`** — зачем нужен случайный IV и как хранится токен.

## 11. Вопросы для самопроверки

1. Почему `createBackup()` разделён на публичный и приватный метод? *(Чтобы `try/finally` с блокировкой был виден отдельно.)*
2. Что будет, если `unlock()` не поставить в `finally`? *(Следующий бэкап навсегда получит «уже выполняется».)*
3. Почему `@Async` нельзя ставить на метод, который вызывается из того же класса? *(Spring не перехватит вызов через прокси, и код выполнится синхронно.)*
4. Зачем `MYSQL_PWD`, если можно передать `--password`? *(Пароль в аргументах виден в списке процессов.)*
5. Почему временная папка удаляется в `finally`? *(Чтобы файлы не остались при ошибке и не забили диск.)*
6. Чем `BackupRunRegistry` отличается от `backup_settings`? *(Первый — состояние в памяти, второй — история в БД.)*
7. Зачем в `restoreImages` проверка `startsWith(uploadDir)`? *(Чтобы архив не мог записать файл вне каталога загрузок.)*

## 12. Мини-словарь

- **Дамп** — SQL-файл с `CREATE TABLE` и `INSERT`, которым можно восстановить базу.
- **tar.gz** — архив (tar) + сжатие (gzip); аналог `.zip`, но в Linux.
- **OAuth-токен** — «пароль для программы», выданный Яндексом; хранится зашифрованным.
- **Polling** — браузер раз в N секунд спрашивает сервер «ну что там?».
- **Идемпотентность** — повторный вызов даёт тот же результат (создание папки, когда она уже есть).
- **Ротация** — удаление старых копий, чтобы не хранить всё вечно.
