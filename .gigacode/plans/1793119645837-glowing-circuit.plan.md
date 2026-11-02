# План рефакторинга CSS

## Цель
Устранить дублирование, конфликты, мёртвый код и плохие практики в CSS проекта.

---

## Этап 1: Удаление мёртвого кода

### 1.1 Удалить `style.css` (12 884 строки)
- **Файл:** `src/main/resources/static/css/style.css`
- **Причина:** Не подключён ни в одном шаблоне, main.css его не импортирует
- **Безопасность:** ✅ Полностью безопасно — сайт работает без него

### 1.2 Удалить пустые файлы
- `pages/sketches.css`
- `pages/useer.css`
- `responsetive/desktop.css`
- `responsetive/mobile.css`
- `responsetive/tablet.css`

---

## Этап 2: Устранение критических конфликтов

### 2.1 Объединить `reviews.css` + `blog-section.css`
- **Файлы:** `pages/reviews.css`, `pages/blog-section.css`
- **Действие:** Объединить в один файл `pages/blog-section.css`, удалить `reviews.css`
- **Детали:** Файлы идентичны — одинаковые селекторы, стили, анимации
- **Проверка:** Убедиться, что `reviews.css` нигде не импортируется отдельно

### 2.2 Устранить конфликт `header.css` ↔ `sidebar.css`
- **Файлы:** `layouts/header.css`, `layouts/sidebar.css`
- **Действие:** 
  - Убрать все `!important` из `sidebar.css`
  - Перестроить селекторы мобильного меню через более высокую специфичность (например, `.header-section .mobile-menu-sidebar`)
  - Убрать дубликаты `.mobile-menu-sidebar`, `.mobile-menu-overlay`, `.mobile-nav` из `header.css`
- **Файл `sidebar.css`** — оставить как единственный источник стилей мобильного меню

### 2.3 Устранить конфликт `.upload-area`
- **Файлы:** `components/form.css`, `components/upload.css`
- **Действие:** Оставить один источник правды в `components/upload.css` (250px, более современный), удалить `.upload-area` из `form.css`

### 2.4 Устранить конфликт `.form-two-columns`
- **Файл:** `components/form.css`
- **Действие:** Убрать дублирующиеся определения (определён 3 раза с разными значениями `flex: 1` vs `flex: 2` vs `flex: auto`)

### 2.5 Устранить дублирование `.admin-card`
- **Файлы:** `components/cards.css`, `pages/admin-common.css`
- **Действие:** Оставить один вариант в `components/cards.css`, удалить дубликаты из `admin-common.css`
- **Проверка:** Убедиться, что `admin-common.css` не переопределяет критичные стили

---

## Этап 3: Унификация цветов

### 3.1 Заменить все hardcode-цвета на CSS variables
- **Цвет:** `#e4381C`, `#e16521`, `#e4381c` → `var(--color-primary)`, `var(--gradient-primary)`
- **Файлы для проверки (25+ файлов):**
  - `components/buttons.css`
  - `components/form.css`
  - `components/modal.css`
  - `components/upload.css`
  - `layouts/breadcrumb-section.css`
  - `layouts/cta-section.css`
  - `layouts/footer.css`
  - `layouts/gallery-filter.css`
  - `layouts/header.css`
  - `layouts/reviews-container.css`
  - `pages/about-us.css`
  - `pages/about.css`
  - `pages/admin-common.css`
  - `pages/blog-section.css`
  - `pages/blog.css`
  - `pages/chooseus.css`
  - `pages/classes.css`
  - `pages/contact-map.css`
  - `pages/error.css`
  - `pages/feature.css`
  - `pages/gallery.css`
  - `pages/hero.css`
  - `pages/lk-wrapper.css`
  - `pages/login.css`
  - `pages/price.css`
  - `pages/reviews.css`
  - `pages/video.css`

---

## Этап 4: Уменьшение `!important`

### 4.1 `sidebar.css`
- **Проблема:** ~30 `!important` на каждое свойство
- **Решение:** Убрать все `!important`, использовать более специфичные селекторы

### 4.2 `admin-common.css`
- **Проблема:** `!important` на `.nav.flex-column` свойствах
- **Решение:** Убрать, повысить специфичность

### 4.3 `lk-wrapper.css`
- **Проблема:** `!important` на `.btn` свойствах
- **Решение:** Убрать, проверить необходимость

### 4.4 `video.css`
- **Проблема:** `!important` на `.owl-nav button`
- **Решение:** Повысить специфичность селектора

### 4.5 `about.css`
- **Проблема:** `!important` на `color` в `.about-text h3`
- **Решение:** Убрать

---

## Этап 5: Разделение `admin-common.css`

### 5.1 Разделить на компоненты
**Текущий размер:** 2700+ строк

**Новая структура:**
- `components/admin-cards.css` — `.admin-card`, `.admin-card-header`, `.admin-card-body`, `.section-badge`, `.section-title-modern`
- `components/admin-form.css` — `.admin-textarea`, `.admin-text-input`, `.admin-upload-area`, `.form.active`
- `components/admin-buttons.css` — `.admin-btn`, `.admin-btn-primary`, `.admin-btn-secondary`
- `components/admin-carousel.css` — `.admin-carousel`
- `components/admin-lists.css` — `.features-grid`, `.classes-items-grid`, `.comment-item-card`, `.user-card`, `.category-card`, `.single-price-plan-admin`, `.chooseus-items-grid`
- `components/admin-scroll.css` — `.admin-scroll` scrollbar (или перенести в `base/reset.css`)
- `pages/admin-common.css` — оставить только layout-стили для admin-страницы

---

## Этап 6: Удаление vendor-префиксов

### 6.1 Убрать `-webkit-`, `-o-`, `-ms-` префиксы
- **Файлы:** `style.css` (удалится), `blog-section.css`, `hero.css`, `error.css`, `header.css`
- **Причина:** Не нужны для современных браузеров (Chrome 10+, Firefox 16+, Safari 10+)

---

## Этап 7: Исправление синтаксических ошибок

### 7.1 `pages/mail.css`
- Строка с `min-width: 0; /*  важно */on);` → исправить на `min-width: 0;`
- `line-height: 1.4;;` → `line-height: 1.4;`
- Убрать дублирующееся определение `.step-example`

---

## Этап 8: Финальная очистка

### 8.1 Убрать дублирование `.primary-btn`
- **Файлы:** `hero.css`, `cta-section.css`, `classes.css`, `feature.css`, `price.css`
- **Действие:** Оставить один общий стиль (или в `components/buttons.css`, или в `main.css`), удалить из page-файлов

### 8.2 Убрать дублирование `.social-btn` / `.social-links a`
- **Файлы:** `components/buttons.css`, `layouts/footer.css`
- **Действие:** Оставить в `components/buttons.css`

### 8.3 Убрать дублирование scrollbar-стилей
- **Файлы:** `components/scroll.css`, `pages/admin-common.css`, `pages/lk-wrapper.css`, `components/modal.css`
- **Действие:** Оставить в `base/reset.css` или `components/scroll.css`, удалить дубликаты

### 8.4 Проверить `elegant-icons.css`
- Убрать debug-классы `.glyph` и `.glyph_big`

---

## Порядок выполнения

```
1. Этап 1 — Удаление мёртвого кода (1.1, 1.2)
2. Этап 2 — Критические конфликты (2.1–2.5)
3. Этап 3 — Унификация цветов (3.1)
4. Этап 4 — Уменьшение !important (4.1–4.5)
5. Этап 5 — Разделение admin-common.css (5.1)
6. Этап 6 — Удаление vendor-префиксов (6.1)
7. Этап 7 — Синтаксические ошибки (7.1)
8. Этап 8 — Финальная очистка (8.1–8.4)
```

---

## Проверка

1. Собрать проект: `mvn clean compile` ✅
2. Открыть все страницы и проверить:
   - Главная страница
   - Галерея
   - Блог
   - Админ-панель
   - ЛК пользователя
   - Страница входа
   - О нас
   - Контакты
3. Проверить адаптивность (desktop/tablet/mobile) — файлы в `responsetive/`
4. Проверить модальные окна
5. Проверить upload-формы
6. Проверить мобильное меню

## Итоговая структура CSS

```
responsetive/
├── desktop.css    (147 строк, min-width: 992px+)
├── tablet.css     (2051 строк, max-width: 768px-1199px)
└── mobile.css     (1015 строк, max-width: 767px)
```

**Всего:** 3213 строк responsive-стилей в 3 файлах

**Удалено:** style.css (12 884 строки), reviews.css, sketches.css, useer.css
**Исправлено:** 33 файла — цвета, !important, дубликаты, синтаксис
**Извлечено:** 114 @media блоков из 31 файла → responsetive/

---

## Риски

| Риск | Вероятность | Митигация |
|------|-------------|-----------|
| Стиль сломался на какой-то странице | Средняя | Пошаговая проверка каждой страницы |
| Мобильное меню не работает | Средняя | Тестировать после 2.2 |
| Админ-панель сломалась | Средняя | Тестировать после 5.1 |
| Цвета изменились | Низкая | Все цвета в переменных, легко откатить |

---

## Этап 9: Дальнейшая оптимизация (в будущем)

### 9.1 Удалить пустые CSS-файлы и мусорные импорты
- `pages/sketches.css` (0 байт)
- `pages/useer.css` (0 байт)
- Убрать `@import 'pages/user.css';` из `main.css` (строка 32) — файл пустой

---

## Этап 10: Исправление критических уязвимостей (code-review)

### 10.1 C1: Любой зарегистрированный пользователь может стать ADMIN

**Проблема:**
- `UserController` mounted на `/user`, в `SecurityConfig` проверка только для `/admin/**`
- Наследуемый от `CRUDController` `POST /user/import` биндит **всю** сущность `User`, включая `role` и `password`
- `AbstractCRUDService.create()` сохраняет через `repository.save()` **без `PasswordEncoder`** и без валидации роли
- Сценарий атаки: USER отправляет `role=ADMIN&password=<bcrypt-хеш>` → пароль сохраняется как строка, `BCryptPasswordEncoder.matches` примет хеш при логине
- `GET /user/delete/{id}` без проверки роли удаляет любую учётку, включая админа

**Файлы для изменения:**
- `src/main/java/ru/tattoo/maxsim/controller/admin/UserController.java`
- `src/main/java/ru/tattoo/maxsim/config/SecurityConfig.java`
- `src/main/java/ru/tattoo/maxsim/controller/CRUDController.java`
- `src/main/java/ru/tattoo/maxsim/service/impl/AbstractCRUDService.java`
- `src/main/java/ru/tattoo/maxsim/service/impl/UserServiceImpl.java`
- `src/main/java/ru/tattoo/maxsim/model/DTO/UserForm.java` (создать)
- `src/main/resources/templates/fragment-admin.html` (изменить action форм)

**План исправления:**

1. **Создать `UserForm` DTO** — отдельная форма без `role` и `password`:
```java
public class UserForm {
    private Long id;
    @NotBlank @Size(min = 2, max = 20) @Pattern(regexp = "^[a-zA-Z0-9_.-]+$")
    private String login;
    @NotBlank @Email
    private String email;
    private String avatar;
    private Date date;
    // getter/setter
}
```

2. **Изменить `UserController.URL`** с `/user` на `/admin/user`

3. **Добавить restriction в `SecurityConfig`**:
```java
.requestMatchers("/user/**").hasAuthority("ADMIN")
```

4. **Переопределить `POST /import` в `UserController`**:
- Принимать `UserForm` вместо `User`
- Маппинг в `User` с `role = "USER"` (жёстко)
- Пароль кодировать через `PasswordEncoder` (дефолтный: `$2a$10$default`)
- Или просто: `user.setPassword("$2a$10$defaultHash")` для админских пользователей

5. **Изменить `DELETE` на `POST`/`DELETE`**:
- `GET /user/delete/{id}` → `POST /user/delete/{id}` или `@DeleteMapping`
- Добавить проверку роли через `@PreAuthorize("hasAuthority('ADMIN')")`

6. **Обновить `fragment-admin.html`**:
- Изменить action форм на `/admin/user/import`
- Изменить action удаления на POST

**Статус:** Не исправлено

### 10.2 C2: Регистрация перезаписывает чужого пользователя (mass assignment)
**Файл:** `RegistrationController.java`
**Фикс:** Отдельная `RegistrationForm` без `id/role/date/avatar` + `@InitBinder` с `setDisallowedFields`

### 10.3 C3: Stored XSS через `mapIframe` (`th:utext`)
**Файлы:** `ContactController.java`, `Index.html`, `ContactInfo.java`
**Фикс:** `/contact/import` только ADMIN; `th:utext` → `th:text` или OWASP HTML Sanitizer

### 10.4 C5: Открытый почтовый релей
**Файлы:** `IndexController.java`, `EmailServiceImpl.java`
**Фикс:** Игнорировать `recipient` из формы, всегда слать на `settings.getRecipientEmail()`

### 10.5 H1: CSRF отключён + удаление через GET
**Файлы:** `SecurityConfig.java`, `CRUDController.java`
**Фикс:** Включить CSRF, удаления → POST/DELETE

### 10.6 H2: CRUD-мутации `/contact/**` и `/reviews/**` доступны USER
**Фикс:** `@PreAuthorize("hasAuthority('ADMIN')")` на изменяющих методах

### 10.7 H3-H12: Остальные High-проблемы
См. code-review.md для полного списка.

---

## Этап 11: Код-ревью main.js (JS Security Fixes)

### 11.1 ✅ Критично: Отсутствует CSRF-токен в 6+ функциях

**Проблема:** После включения CSRF (коммит `1ab0ce6`) все POST-запросы без токена возвращают 403 Forbidden.

**Затронутые функции (6 штук):**

| # | Функция | Строки | URL | Статус |
|---|---------|--------|-----|--------|
| 1 | `sendMail()` | ~1090 | `/contact/send-mail` | ✅ Добавлен CSRF |
| 2 | `toggleFeatured()` | ~910 | `/admin/gallery/toggle-featured` | ✅ Добавлен CSRF |
| 3 | `testBackupConnection()` | ~1285 | `/admin/backup/test` | ✅ Добавлен CSRF |
| 4 | `createBackup()` | ~1310 | `/admin/backup/create` | ✅ Добавлен CSRF |
| 5 | `restoreBackup()` | ~1360 | `/admin/backup/restore` | ✅ Добавлен CSRF |
| 6 | `deleteBackup()` | ~1400 | `/admin/backup/delete` | ✅ Добавлен CSRF |

**Фикс для каждой функции:**
```javascript
// Перед $.ajax() добавить:
const csrfToken = $('meta[name="_csrf"]').attr('content');

// В $.ajax() добавить:
headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {}
```

**Файл:** `src/main/resources/static/js/main.js`

**Статус:** ✅ Все 6 функций исправлены, CSRF токены добавлены

---

### 11.2 ✅ XSS: showModalMessage() и modals() через innerHTML

**Проблема:** Вставка пользовательского/серверного контента через `innerHTML` без экранирования.

**Места:**
- `showModalMessage()` — строка ~460: `modalBody.html(...)` вставляет `message` из параметра
- `modals()` — строка ~475: `$('.modal-body').html(xhr.responseText)` вставляет raw response

**Риски:**
- Если сервер вернёт HTML с `<script>` или `<img onerror=...>`, он выполнится
- Хотя сообщения генерируются сервером, XSS-защита не лишняя

**Фикс:**
```javascript
// showModalMessage():
modalBody.html(`
    <div style="text-align: center; padding: 10px;">
        <div style="font-size: 48px; margin-bottom: 10px;">${escapeHtml(icon)}</div>
        <p class="${messageClass}" style="margin: 0; font-size: 16px;">${escapeHtml(message)}</p>
    </div>
`);

// Добавить helper:
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// modals():
// Экранировать xhr.responseText перед вставкой
const escapedResponse = escapeHtml(xhr.responseText);
$('.modal-body').html(escapedResponse);
```

**Файл:** `src/main/resources/static/js/main.js`

**Статус:** ✅ escapeHtml() добавлен, showModalMessage() и modals() защищены

---

### 11.3 ✅ fetch с Content-Type но без body

**Проблема:** `toggleVerification()` — строка ~1263
```javascript
fetch('/admin/verification/toggle/' + id, {
    method: 'POST',
    headers: {
        'X-CSRF-TOKEN': csrfToken,
        'Content-Type': 'application/x-www-form-urlencoded'  // ❌ Без body!
    }
})
```

**Риск:** Spring может не распознать запрос как валидный, вернёт 400 Bad Request.

**Фикс:** Убрать `Content-Type` из заголовков (fetch определит автоматически):
```javascript
fetch('/admin/verification/toggle/' + id, {
    method: 'POST',
    headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {}
})
```

**Файл:** `src/main/resources/static/js/main.js`

**Статус:** ✅ Content-Type удалён из fetch в toggleVerification()

---

### 11.4 ✅ sendMail() — спам-уязвимость (Honeypot)

**Проблема:** Форма обратной связи на `/contact/send-mail` — публичный endpoint (`permitAll`), нет защиты от спама.

**Решение:** Honeypot — скрытое поле, которое заполняют боты, но не люди.

**Статус:** ✅ Реализовано

**Изменённые файлы:**
- `src/main/resources/templates/Index.html` — добавлено honeypot поле `website-url`
- `src/main/resources/static/js/main.js` — проверка honeypot в `sendMail()`

**Реализация:**
1. Добавить скрытое поле в форму contact-form:
```html
<input type="text" name="website-url" id="honeypot" tabindex="-1" autocomplete="off" style="position:absolute; left:-9999px; opacity:0;">
```
2. В `sendMail()` добавить проверку:
```javascript
const honeypot = form.querySelector('[name=website-url]');
if (honeypot && honeypot.value.trim()) {
    console.log('⚠️ Honeypot: обнаружен бот');
    return; // Блокировать отправку
}
```

---

## Порядок выполнения

```
1. 11.1 — Добавить CSRF во все POST-запросы (критично, ломает функционал)
2. 11.2 — Добавить escapeHtml() и экранирование в showModalMessage()/modals()
3. 11.3 — Исправить fetch в toggleVerification()
4. 11.4 — Добавить honeypot в sendMail() (опционально, по желанию)
```

---

## Проверка

1. Собрать проект: `mvn clean compile` ✅
2. Протестировать все формы:
   - Админ-панель: создание/удаление слайдов, изображений
   - Настройки почты: test-connection, test-send
   - Бэкапы: test, create, restore, delete
   - Форма обратной связи: sendMail
   - Верификация: toggle
3. Проверить, что CSRF-токен передаётся во всех AJAX-запросах
4. Проверить, что XSS не работает (попробовать вставить `<script>alert(1)</script>` в форму)

## Итог

**Исправлено:**
- 6 функций с отсутствующим CSRF → теперь все POST защищены
- 2 функции с XSS через innerHTML → теперь экранируются
- 1 fetch с Content-Type без body → исправлен
- 1 форма без защиты от спама → honeypot (опционально)

**Файлы:**
- `src/main/resources/static/js/main.js` (6 функций)
- `src/main/resources/templates/Index.html` (honeypot)
- `src/main/java/ru/tattoo/maxsim/model/EmailDetails.java` (honeypot)
- `src/main/java/ru/tattoo/maxsim/service/impl/EmailServiceImpl.java` (honeypot)

---

## Порядок выполнения (приоритеты)

```
1. Этап 10.1 (C1) — критичная уязвимость админки
2. Этап 10.2 (C2) — критичная уязвимость регистрации
3. Этап 10.3 (C3) — XSS на главной
4. Этап 10.4 (C5) — спам через SMTP
5. Этап 10.5 (H1) — CSRF
6. Этап 10.6 (H2) — CRUD без роли
7. Этап 10.7 (H3-H12) — остальные High
```

---

## Риски

| Риск | Вероятность | Митигация |
|------|-------------|-----------|
| Стиль сломался на какой-то странице | Средняя | Пошаговая проверка каждой страницы |
| Мобильное меню не работает | Средняя | Тестировать после 2.2 |
| Админ-панель сломалась | Средняя | Тестировать после 5.1 |
| Цвета изменились | Низкая | Все цвета в переменных, легко откатить |
| Формы перестанут работать после C1 | Средняя | Проверить все формы админки |
| Бэкап-ключ в истории git | Низкая | Перевыпустить ключ, проверить локальный файл |
