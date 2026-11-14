# SPEC_MODALS.md — подготовка к рефакторингу модальных окон

> **Статус: только анализ.** HTML, CSS и JS не изменялись. План ниже — предложение; перенос выполняется
> только после подтверждения владельца. Правила, которым должен следовать рефакторинг:
> `.dsh/skills/modal-refactor/SKILL.md`.

## 0. Как собирались данные

- `code_index`: 258 файлов, 1524 символа; индексируются только `java` и `javascript` — **HTML-шаблоны
  в индекс не попадают**.
- `code_search("modal")` нашёл JS-символы: `modals()`, `showModalMessage()`, `initVideoModal()`,
  `closeVideoModal()`.
- `code_refs("showModalMessage")` — 27 вызовов в `backup.js`, `gallery.js`, `image.js`, `mail.js`,
  `forms.js`: модалка сообщений используется глобально.
- Шаблоны искались `grep`/чтением по паттернам:
  `class="…modal…"`, `video-modal`, `popup`, `dialog`, `id="…modal…"`, `th:replace`, `th:fragment`,
  `data-toggle="modal"`, `data-bs-*`, `style=`.
- CSS — `grep` по `.modal`, `.video-modal`, `.mail-instruction-modal`, `.gallery-modal`,
  `.btn-close-custom`; Java — `grep "modal::"`.

## 1. Что считаем модалкой

Модалка — окно поверх страницы, блокирующее взаимодействие с фоном:

- Bootstrap-модалка (`class="modal fade"`, `role="dialog"`);
- кастомный overlay (`.video-modal`, открывается классом `.active`);
- диалог/попап, если он ведёт себя как модальное окно.

**Не считаем модалками** (вне скоупа):

| Что | Где | Почему |
|---|---|---|
| `image-popup` | `fragments.html:265,386`, `about-me.html`, `lk.html`, `reviews.html` | Magnific Popup — лайтбокс изображения, не диалог |
| `upload-overlay` | `fragment-admin/*.html` (home, gallery, sketches, blog, reviews, settings) | hover-оверлей загрузки |
| `tattoo-overlay` | `fragment-lk.html:33` | hover-оверлей карточки |
| `GalleryController.reviewsModal` | `controller/client/GalleryController.java:90` | HTTP-метод, не UI |

## 2. Инвентаризация модалок (как есть)

| ID | Где лежит сейчас | Оболочка | Классы оболочки | Кто использует |
|---|---|---|---|---|
| **M1** `#myModal` (сообщение) | `modal.html:10-39`, фрагмент `modal`; вложенный `modal-body` (`:24-32`). Дубликат: `blog.html:55-79` | Bootstrap + кастомная кнопка | `modal fade`, `modal-dialog`, `modal-content`, `modal-header`, `modal-title`, `btn-close btn-close-custom`, `delete-button`, `modal-body`, `container-fluid`, `modal-footer`, `btn btn-secondary`; атрибуты `data-bs-backdrop/keyboard/dismiss` | `admin.html:98`, `lk.html:32`; Java: `modal::modal-body` (9 мест); JS `forms.js` |
| **M2** `#videoModal` (видео) | `modal.html:44-65`, фрагмент `video-modal` | Кастомная | `video-modal`, `video-modal-content`, `video-modal-header`, `video-modal-close`, `video-modal-body`, `video-modal-player`; id `videoModalTitle`, `videoModalIframe`, `videoModalClose`; состояние `.active` | `Index.html:426`; JS `video.js` |
| **M3** `#mailSettingsModal` (инструкция по почте) | `modal.html:67-493`, фрагмент `settings-video-modal` | Bootstrap + кастомная обёртка | `modal fade mail-instruction-modal`, `modal-dialog modal-xl modal-dialog-scrollable`, `modal-content`, `modal-header`, `modal-title`, `close`, `modal-body`, `modal-footer`, `btn btn-secondary`. Контент: `instruction-grid`, `instruction-step`, `step-number`, `step-content`, `service-badges`, `service-badge`, `step-tip`, `dns-section`, `dns-record*`, `error-codes-grid`, `error-code-card`, `faq-list/item/answer`, `example-*`, `copy-btn-inline`, `badge-type`, `code` | `fragment-admin/settings.html:1336`; триггер `:1321-1323` (`data-toggle`/`data-target`) |
| **M4** `#infoModal` (выбор изображения) | `fragments.html:179-250`, фрагмент `modal` | Bootstrap + кастомная кнопка | `modal fade`, `modal-dialog modal-xl modal-dialog-centered`, `modal-content`, `modal-header`, `modal-title`, `delete-button btn-close-custom`, `modal-body`, `gallery-filter-modal`, `gallery-filter`, `gallery-modal-item`, `gallery-modal-img`, `modal-footer`, `blog-option`, `blog-pagination`, `page-nav`, `blog-option-right`, `show-result*`; inline `onclick="closeModal()"` | `lk.html:31`, `reviews.html:39`; JS `image.js` |
| **M5** `#myModal` (дубль) | `blog.html:55-79`, inline, не фрагмент | Bootstrap + кастомная кнопка | те же, что у M1; тело пустое; `btn-close btn-close-custom`, `data-bs-*` | только `blog.html`; `forms.js` (`showModalMessage` ищет `#myModal`) |

### Особенности и коллизии

1. **`#myModal` объявлен дважды** — во фрагменте (`modal.html`) и отдельно в `blog.html`. Сейчас страницы
   не включают оба одновременно, но id не уникален на уровне проекта.
2. **Имя фрагмента `modal` занято дважды**: в `modal.html` это `#myModal`, в `fragments.html` — `#infoModal`.
   Включения различаются только именем файла (`modal::modal` vs `fragments::modal`) — легко ошибиться.
3. **`settings-video-modal` содержит не видео**, а инструкцию по почте — историческое имя.
4. **Bootstrap 4.4.1 vs атрибуты BS5.** Загружен `bootstrap.min.js` 4.4.1, но в M1/M4/M5 используются
   `data-bs-backdrop`, `data-bs-keyboard`, `data-bs-dismiss`, `btn-close` (это BS5). В M3 — правильные для
   BS4 `data-dismiss`/`close`; триггер M3 использует `data-toggle` (BS4). Итог: кнопки с `data-bs-dismiss`
   в BS4 не закрывают модалку; закрытие сейчас работает только через JS `.modal('hide')`.
5. **`closeModal()`** в M4 (`fragments.html:186`) — функции нет в JS (`grep` по `static/js` пуст; есть только
   Selenium-метод `AdminPage.closeModal()` в тестах). Кнопка закрытия фактически не работает.
6. **`h7`** в M1 — несуществующий тег.
7. **Неиспользуемый `id="hide"`** у кнопки закрытия M1.
8. **Смешивание систем**: M1/M4/M5 — Bootstrap + `delete-button`/`btn-close-custom`; M3 — Bootstrap +
   `mail-instruction-modal`; M2 — кастомные классы, но не БЭМ.

## 3. Где сейчас лежат стили

| Файл | Строки | Что | Куда должно переехать |
|---|---|---|---|
| `static/css/components/modal.css` | 1-165 | базовые `.modal-content/header/title/body`, `.delete-button`, `.btn-close-custom`, `.gallery-filter-modal`, `.gallery-modal-*` | остаётся, реорганизуется по секциям |
| `static/css/pages/video.css` | 334-420 | `.video-modal*`, `@keyframes videoModalIn` | в `modal.css` как `.custom-modal--video` |
| `static/css/pages/mail.css` | 599-680 | `.mail-instruction-modal .modal-*`, `.close`, `.modal-footer .btn-secondary` | в `modal.css` (оболочка) |
| `static/css/pages/mail.css` | ~680-1200 | контент инструкции: `.instruction-*`, `.service-badge*`, `.dns-*`, `.error-code*`, `.faq-*`, `.example-*`, `.copy-btn-inline` (секции «Details inside modal» :815, «FAQ inside modal» :1115) | в `modal.css`, секция контента |
| `static/css/responsetive/tablet.css` | 141-180, 1600-1616 | адаптив `.modal-*`, `.gallery-modal-*`, `.video-modal-*` | в `modal.css` (media queries) |
| `static/css/responsetive/mobile.css` | 43-56 | адаптив `.gallery-modal-img`, `.modal-footer …` | в `modal.css` (media queries) |

`main.css` уже импортирует: `components/modal.css` (:13), `pages/video.css` (:39), `pages/mail.css` (:46).

### Инлайн-стили и стили из JS (нарушают правило)

| Место | Что | Решение |
|---|---|---|
| `modal.html:223` | `style="font-size:11px;"` у иконки | класс в `modal.css` |
| `modal.html:434` | `style="border-top-color: var(--m-border);"` | класс в `modal.css` |
| `fragment-admin/settings.html:1322` | `style="cursor: pointer;"` на кнопке-триггере | убрать (курсор у `button` и так pointer) |
| `fragments.html:186` | `onclick="closeModal()"` | удалить; закрытие через `data-dismiss` (BS4) |
| `forms.js:139-148` | inline-стили в HTML, который вставляется в `.modal-body` | классы `.modal-message`, `.modal-message__icon`, `.modal-message__text` |
| `video.js:77,84` | `document.body.style.overflow` | класс `body.modal-open` + CSS |

## 4. Целевая структура

```
src/main/resources/templates/fragments/modals/
├── message.html          # M1 #myModal, фрагменты message + message-body
├── video.html            # M2 #videoModal, фрагмент video
├── image-picker.html     # M4 #infoModal, фрагмент image-picker
└── mail-instruction.html # M3 #mailSettingsModal, фрагмент mail-instruction
```

| Файл | Фрагмент | id | Система классов | Комментарий |
|---|---|---|---|---|
| `message.html` | `message`, `message-body` | `myModal` | **Bootstrap 4.4.1** | id и `.modal-body`/`.modal-footer .btn-secondary` использует `forms.js` и Selenium `AdminPage`; менять только вместе с ними |
| `video.html` | `video` | `videoModal` | **Custom BEM** `.custom-modal.custom-modal--video` | уже кастомный overlay; переименовать классы и состояние `.active` → `.custom-modal--open` |
| `image-picker.html` | `image-picker` | `infoModal` | **Bootstrap 4.4.1** | убрать inline `onclick`, закрытие через `close` + `data-dismiss` |
| `mail-instruction.html` | `mail-instruction` | `mailSettingsModal` | **Custom BEM** `.custom-modal.custom-modal--mail` (рекомендуется) | альтернатива — чистый Bootstrap; решается до начала шага 5 |

### Соответствие классов

**Bootstrap → Bootstrap 4.4.1 (M1, M4):**

- `data-bs-backdrop` → `data-backdrop`, `data-bs-keyboard` → `data-keyboard`;
- `data-bs-dismiss="modal"` → `data-dismiss="modal"`;
- `btn-close`/`btn-close-custom`/`delete-button` → `close` (BS4);
- `h7` → `h5`.

**Кастомные (M2/M3):**

- `.video-modal` → `.custom-modal.custom-modal--video`, `.video-modal-content` → `.custom-modal__content`,
  `…-header` → `__header`, `…-body` → `__body`, `…-close` → `__close`, `…-player` → `__player`;
- `.video-modal.active` → `.custom-modal--open` (или `.custom-modal--video.custom-modal--open`);
- `.mail-instruction-modal` → `.custom-modal.custom-modal--mail`; `modal-dialog` → `__dialog`,
  `modal-content` → `__content`, `modal-header` → `__header`, `modal-title` → `__title`,
  `close` → `__close`, `modal-body` → `__body`, `modal-footer` → `__footer`;
- контентные классы (`instruction-*` и т.п.) остаются, но стилизуются внутри `modal.css` и не трогают
  оболочку.

## 5. Пошаговый план переноса (выполнять после подтверждения)

> Рекомендуется отдельная ветка: `git switch -c refactor/modals`.

**Шаг 0. Подготовка CSS (без изменения разметки).**
В `components/modal.css` добавить секции: `Bootstrap overrides`, `Custom modal (BEM)`, `Message`,
`Responsive`, `body.modal-open`. Убедиться, что визуально ничего не изменилось.

**Шаг 1. M1 — сообщение (`#myModal`).**
1. Создать `fragments/modals/message.html` с фрагментами `message` и `message-body`; перевести оболочку на
   Bootstrap 4.4.1, убрать `delete-button`/`btn-close-custom`/`#hide`, `h7` → `h5`.
2. Обновить включения: `admin.html:98`, `lk.html:32`; в `blog.html` удалить inline-блок `55-79` и добавить
   `th:replace`.
3. Java: заменить `modal::modal-body` → `fragments/modals/message :: message-body` в
   `GlobalExceptionHandler` (:96,108,117,124,132), `LkController` (:184,189,217),
   `GalleryAdminController` (:85).
4. JS: в `forms.js` скоупить `#myModal .modal-body`, обновить селектор кнопки закрытия, перенести inline-стили
   сообщения в CSS-классы.
5. CSS: удалить `.delete-button`/`.btn-close-custom`, если больше не используются.
6. Проверить: `/admin` (любая форма с ответом), `/lk`, `/blog`, ошибки формы (модалка с `modal-body`).

**Шаг 2. M2 — видео (`#videoModal`).**
1. Создать `fragments/modals/video.html`; переименовать классы в BEM.
2. `Index.html:426` → `~{fragments/modals/video :: video}`.
3. JS `video.js`: `classList.add/remove('custom-modal--open')`, блокировка скролла через `body.modal-open`.
4. CSS: перенести `.video-modal*` и keyframes из `pages/video.css` в `modal.css`; удалить старые правила
   и адаптив из `responsetive/tablet.css:1600-1616`.
5. Проверить: главная → карточка видео, закрытие кнопкой/фоном/`Esc`, мобильная ширина.

**Шаг 3. M4 — выбор изображения (`#infoModal`).**
1. Создать `fragments/modals/image-picker.html` из `fragments.html:179-250`.
2. `lk.html:31`, `reviews.html:39` → `~{fragments/modals/image-picker :: image-picker}`.
3. Убрать `onclick="closeModal()"`; закрытие — `close` + `data-dismiss`.
4. Перенести `.gallery-modal-*`, `.gallery-filter-modal` из `components/modal.css`/адаптива в секцию
   image-picker; удалить дубли.
5. Проверить: `/lk` и `/reviews` → «выбрать изображение», закрытие, выбор картинки (`image.js`),
   пагинация в футере модалки.

**Шаг 4. M3 — инструкция по почте (`#mailSettingsModal`).**
1. Создать `fragments/modals/mail-instruction.html` (рекомендуется custom BEM).
2. `fragment-admin/settings.html:1336` → `~{fragments/modals/mail-instruction :: mail-instruction}`;
   триггер `:1321-1323` перевести на `data-modal-open="#mailSettingsModal"` (или оставить BS4
   `data-toggle`, если выбран Bootstrap-вариант).
3. JS: добавить/расширить модуль открытия-закрытия кастомных модалок (клик по фону, `Esc`, `body.modal-open`).
4. CSS: перенести оболочку из `pages/mail.css:599-680` и контент инструкции в `modal.css`; убрать inline-стили
   `modal.html:223,434`.
5. Проверить: админка → Почта → «Как настроить почту?», вкладки, скролл, закрытие, мобильная ширина.

**Шаг 5. Очистка.**
1. Удалить `modal.html` (после переноса всех трёх фрагментов).
2. Удалить фрагмент `modal` из `fragments.html`.
3. Удалить старые CSS-секции из `pages/video.css`, `pages/mail.css`, `responsetive/*`.
4. `grep`-проверки (см. раздел 8).
5. Запустить рендер страниц и тесты; обновить `SPEC_MODALS.md` (статус «готово»).

## 6. Где обновить `th:replace`

| Файл:строка | Сейчас | После |
|---|---|---|
| `admin.html:98` | `~{modal::modal}` | `~{fragments/modals/message :: message}` |
| `lk.html:31` | `~{fragments::modal}` | `~{fragments/modals/image-picker :: image-picker}` |
| `lk.html:32` | `~{modal::modal}` | `~{fragments/modals/message :: message}` |
| `reviews.html:39` | `~{fragments::modal}` | `~{fragments/modals/image-picker :: image-picker}` |
| `Index.html:426` | `~{modal::video-modal}` | `~{fragments/modals/video :: video}` |
| `fragment-admin/settings.html:1336` | `~{modal::settings-video-modal}` | `~{fragments/modals/mail-instruction :: mail-instruction}` |
| `blog.html:55-79` | inline `#myModal` | `<div th:replace="~{fragments/modals/message :: message}"></div>` |
| `fragments.html:179-250` | фрагмент `modal` | удалить (перенесён в image-picker) |
| `modal.html` | три фрагмента | удалить после шагов 1-4 |

### Java: view-имена, которые нужно поменять

| Файл:строка | Сейчас |
|---|---|
| `exceptions/GlobalExceptionHandler.java:96,108,117,124,132` | `return "modal::modal-body";` |
| `controller/client/LkController.java:184,189,217` | `return "modal::modal-body";` |
| `controller/admin/GalleryAdminController.java:85` | `return "modal::modal-body";` |

После переноса: `"fragments/modals/message :: message-body"` (или `modal-body`, если решим сохранить имя).

### JS и тесты, которые зависят от id/классов

| Место | Зависимость | Что учесть |
|---|---|---|
| `static/js/modules/forms.js:133-167` | `#myModal`, `.modal-body`, `.btn-close-custom`, `.btn-secondary` | скоупить селекторы, обновить кнопку закрытия |
| `static/js/modules/image.js:110-123` | `#infoModal`, `.modal('hide')` | оставить id `infoModal` |
| `static/js/modules/video.js:61-100` | `#videoModal`, `#videoModalIframe`, `#videoModalTitle`, `#videoModalClose`, `.active`, `body.style.overflow` | обновить класс состояния и блокировку скролла |
| `static/js/modules/gallery.js:95` | `.modal-img` | **селектор уже мёртвый** (в шаблонах только `gallery-modal-img`) — исправить или удалить |
| `src/test/java/.../UI/page/AdminPage.java:200-210,406-408,1123-1133` | `#myModal`, `.modal-body .text-success/.text-danger`, `.modal-footer .btn-secondary` | сохранить или обновить page object в том же шаге |
| `UI/e2e/AdminFlowE2ETest`, `ContentFlowE2ETest` | `adminPage.closeModal()` | проверить после изменения футера/кнопки |

## 7. Риски

1. **Bootstrap 4.4.1 vs BS5-атрибуты** — сейчас кнопки `data-bs-dismiss` не работают. Решение: зафиксировать
   BS4 (`data-dismiss`, `close`) в этом рефакторинге; апгрейд Bootstrap — отдельная задача.
2. **Дубликаты** `#myModal` и имени фрагмента `modal` — устраняются в шаге 1/3; до этого любые новые
   включения запрещены.
3. **Глобальные селекторы** `.modal-body`/`.btn-secondary` в JS могут задеть чужую модалку после переноса.
4. **UI-тесты** завязаны на `#myModal` и `.modal-footer .btn-secondary` — ломать их без обновления нельзя.
5. **`closeModal()`** не определён — не переносить эту ошибку в новый файл.
6. **Модалка почты** использует CSS-переменные `--m-*` из `pages/mail.css`; при переносе стилей нужно
   сохранить переменные (перенести `:root`-блок или оставить его в `mail.css`).
7. **Доступность**: у модалок нет `aria-modal="true"`, фокуса-ловушки и возврата фокуса; рекомендуется
   добавить при переносе (не блокирует рефакторинг).
8. **Скролл body** управляется из JS инлайн-стилем — заменить классом, иначе после закрытия возможны
   «залипшие» стили.
9. **`image-picker` зависит от модели** `${gallery...}` — страницы `lk`/`reviews` обязаны её передавать
   (сейчас передают); при переносе не потерять `th:each`-переменные.

## 8. Критерии готовности

```bash
# модалки только в fragments/modals
grep -rn 'class="[^"]*modal' src/main/resources/templates --include=*.html

# нет inline-стилей в модалках
grep -rn 'style=' src/main/resources/templates/fragments/modals

# нет старых view-имён
grep -rn 'modal::' src/main/java src/main/resources/templates

# не осталось старых классов и файлов
grep -rn 'video-modal\|mail-instruction-modal\|btn-close-custom\|delete-button\|settings-video-modal' \
  src/main/resources

# стили модалок только в components/modal.css
grep -rn '^\.\(video-modal\|mail-instruction-modal\)' src/main/resources/static/css/pages \
  src/main/resources/static/css/responsetive
```

Ручная проверка: `/admin` (все вкладки), `/lk`, `/reviews`, `/blog`, `/` (видео), вкладка «Почта» в
админке; закрытие кнопкой, `Esc`, кликом по фону; блокировка скролла; ширина 375 px и 768 px.

## 9. Открытые вопросы (нужно решение до шагов 1 и 4)

1. Bootstrap: остаёмся на **4.4.1** (рекомендуется) или планируем апгрейд до 5? От этого зависят
   `data-*`-атрибуты и классы закрытия.
2. `mail-instruction`: **custom BEM** (рекомендуется, т.к. стили уже сильно кастомные) или чистый Bootstrap?
3. Переименовывать ли фрагмент `modal-body` → `message-body`? Это 9 правок в Java, зато семантика чище.
4. `blog.html`: удалить inline-модалку и включить общий фрагмент (рекомендуется) или оставить дубль?
5. Включать ли Magnific Popup (`.image-popup`) в этот рефакторинг? Рекомендация — **нет**, это лайтбокс.
6. Сохранять ли `id="myModal"` ради UI-тестов или переименовать в `messageModal` с обновлением
   `AdminPage` и JS?
