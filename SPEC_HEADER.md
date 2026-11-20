# SPEC_HEADER.md — рефакторинг хедера и мобильного меню

> **Статус:** P0-фикс меню уже применён (см. §5). Структурный рефакторинг хедера — по плану ниже, после
> подтверждения. Правила: `.dsh/skills/header-refactor/SKILL.md`. Промт: `.dsh/skills/header-refactor/PROMPT.md`.

## 1. Где лежит хедер

Сейчас хедер — часть `templates/fragments.html`:

| Что | Строки | Фрагмент |
|---|---|---|
| Обёртка хедера + мобильное меню | 9-93 | `th:fragment="header"` (строка 10) |
| Логотип | 14-18 | вложенный `th:fragment="logo"` (15) |
| Десктопное меню | 21-32 | `<nav class="mainmenu">`, 7 ссылок |
| Соцсети/авторизация (desktop) | 35-52 | `.top-social` |
| Бургер | 49-52 | `.mobile-menu-toggle-icon` (сейчас с `aria-*`) |
| Оверлей | 57 | `.mobile-menu-overlay` |
| Сайдбар | 58-91 | `.mobile-menu-sidebar` (`id="mobile-menu-sidebar"`) |
| Мобильное меню | 64-74 | `<nav class="mobile-nav">`, те же 7 ссылок |
| Соцсети/авторизация (mobile) | 75-90 | `.mobile-menu-social` |

Хедер подключают 11 страниц:

`Index.html:67`, `about-me.html:35`, `blog.html:53`, `contact.html:54`, `gallery.html:56`,
`lk.html:27`, `login.html:43`, `registration.html:43`, `reviews.html:37`, `sketches.html:57`,
`admin.html:36` — все через `~{fragments::header}`.

## 2. Дублирование и inline-код

| Что | Где | Проблема |
|---|---|---|
| Навигация | desktop 21-32, mobile 64-74 | 7 ссылок скопированы дважды; пункты расходятся при правке |
| Соцсети/авторизация | 35-52 и 75-90 | два набора кнопок, две формы logout |
| Logout-форма | 38-45, 78-87 | дважды `POST /logout` + CSRF + inline-стили |
| Inline-стили | `fragments.html:40,42,80,82` | `display:inline`, `background:none`, `padding:0` |
| Логотип | 15-18 и 59-61 | два `<img>`, у мобильного нет `id`; `th:each` + `th:if` |

Модельные зависимости: `setting` (логотип), `servletPath` (активный пункт; задаётся
`GlobalController:33`), `_csrf` (logout).

## 3. CSS

| Файл | Строк | `!important` | Что |
|---|---:|---:|---|
| `layouts/header.css` | 482 | 1 | бургер, сайдбар, оверлей, навигация, соцсети, `body.menu-open` |
| `layouts/sidebar.css` | 161 | **24** | те же `.mobile-menu-*`/`.mobile-nav*` — дубль, перекрывает header.css |
| `responsetive/tablet.css` | — | — | `.mobile-menu-toggle-icon` (222-245) |
| `responsetive/mobile.css` | — | — | `.mobile-menu-toggle-icon` (89-100), `.mobile-menu-sidebar` (95, 203), соцсети (212-215) |

`main.css` импортирует `layouts/header.css` (19), затем `layouts/sidebar.css` (21): дубли из
`sidebar.css` выигрывают за счёт `!important`. Пересечение селекторов — 11 штук:
`.mobile-menu-close`, `.mobile-menu-close:hover`, `.mobile-menu-logo img`, `.mobile-menu-overlay.active`,
`.mobile-menu-social a`, `.mobile-menu-social a:hover`, `.mobile-nav ul`, `.mobile-nav ul li`,
`.mobile-nav ul li a`, `.mobile-nav ul li a:hover`, `.mobile-nav ul li.active a`.

`body.menu-open { overflow: hidden; }` продублирован в обоих файлах.

## 4. JS: почему меню работало не везде

`initMobileMenu()` находится в `static/js/modules/ui.js` (строки 34-75 после фикса).
`main.js` вызывает её в `$(document).ready` только при `typeof initMobileMenu === 'function'`
(`main.js:51`), т.е. без модуля — молча пропускает.

**Матрица загрузки до фикса:**

| Страницы | Что подключали | Меню |
|---|---|---|
| `admin.html` | `modules/ui.js` + `main.js` | работало |
| `Index.html`, `contact.html` | `fragments/index/scripts.html` (ui.js, video.js, image.js, main.js) | работало (после переноса Index) |
| `about-me`, `blog`, `gallery`, `lk`, `login`, `registration`, `reviews`, `sketches` | только `main.js` | **не работало** |

**Root cause:** модуль `ui.js` не был подключён на 8 страницах. Это не ошибка функции, а отсутствие
загрузки модуля.

## 5. Что уже исправлено (P0)

1. На 8 страниц добавлен `<script th:src="@{/static/js/modules/ui.js}"></script>` **до** `main.js`
   (`about-me`, `blog`, `gallery`, `lk`, `login`, `registration`, `reviews`, `sketches`).
   `admin.html` уже имел модуль; `Index.html`/`contact.html` получают его через
   `fragments/index/scripts.html`.
2. `initMobileMenu()` переписан:
   - убран мёртвый `slicknav`-вызов на несуществующий `.mobile-menu`;
   - добавлены `openMenu()`/`closeMenu()`, переключение (а не только открытие);
   - закрытие по `.mobile-menu-close`, `.mobile-menu-overlay`, **клику по ссылке** `.mobile-nav a`,
     `Escape`;
   - `aria-expanded` на бургере, `body.menu-open`;
   - идемпотентность через `.off('.mobileMenu').on(...)`.
3. В `fragments.html`: бургер получил `aria-label`, `aria-expanded="false"`,
   `aria-controls="mobile-menu-sidebar"`; сайдбар — `id`; кнопка закрытия — `type="button"` и
   `aria-label`.

Проверено статически: `node --check ui.js` — ок; на всех 11 страницах с хедером `modules/ui.js`
идёт до `main.js` (или через `scripts.html`); `resources:resources` — ок. HTTP-смоук не выполнен:
приложение на `:8081` в момент проверки не было запущено.

## 6. Целевая структура

```
templates/fragments/
├── header/
│   ├── header.html   # fragment header: обёртка, logo, nav, socials, mobile-menu
│   ├── nav.html      # fragment nav(menuClass): ОДИН список ссылок
│   └── socials.html  # fragment socials: соцсети + login/logout/admin/lk
└── scripts.html      # (или public/scripts.html) единый набор vendor + modules + main.js
```

- Десктоп: `~{fragments/header/nav :: nav('mainmenu')}`; мобилка:
  `~{fragments/header/nav :: nav('mobile-nav')}` — один источник.
- `socials` вызывается дважды с модификатором контейнера, без копирования формы logout.
- Страницы обновляют include: `~{fragments/header/header :: header}` (11 файлов).
- `fragments.html` больше не содержит хедер (остаются cta/footer/галерея или они тоже выносятся
  отдельными задачами).
- `scripts.html` переименовать/вынести из `fragments/index/`, т.к. он общий для публичных страниц
  (открытый вопрос).

## 7. План этапов (после P0)

### Этап 1. Вынести хедер из `fragments.html`

1. Создать `fragments/header/header.html`, перенести строки 9-93, оставить `th:fragment="header"`.
2. Создать `nav.html` (`nav(menuClass)`) и `socials.html`; заменить две копии навигации/соцсетей
   вызовами.
3. Обновить 11 страниц: `~{fragments/header/header :: header}`.
4. Удалить старый фрагмент из `fragments.html`; `grep -rn "fragments::header"` → пусто.
5. Проверить все страницы, активный пункт (`servletPath`), CSRF logout.

### Этап 2. Убрать inline-стили

- `.logout-form` уже имеет `display:inline` в `components/buttons.css` — убрать `style="display:inline;"`.
- Для кнопок logout ввести `.logout-btn` (фон/бордер/паддинги в CSS) и убрать 4 inline-стиля.
- Проверить внешний вид desktop/mobile.

### Этап 3. CSS: один владелец

1. Удалить дубли `.mobile-menu-*`/`.mobile-nav*` из `layouts/sidebar.css`; оставить файл только для
   того, что действительно относится к sidebar (или удалить, если пуст).
2. Убрать `!important` (24) после удаления дублей; z-index — через переменные/явные слои.
3. Удалить дубли `body.menu-open` (оставить в `header.css`).
4. Перенести адаптивные правила бургер-меню из `responsetive/*` в `header.css` (или оставить, но без
   дублей).

### Этап 4. Доступность и поведение

- Фокус на кнопку закрытия при открытии и возврат на бургер при закрытии.
- `aria-hidden` на сайдбаре или `inert` (по поддержке).
- Проверить `Escape`, клик по фону, клик по ссылке, повторное открытие.
- Убедиться, что `body.menu-open` не «залипает» после перехода.

### Этап 5. Единый scripts-фрагмент

- Переименовать `fragments/index/scripts.html` → `fragments/scripts.html` (или `public/scripts.html`),
  обновить Index/contact и, при желании, остальные страницы.
- Удалить копипасту `<script ... ui.js>` из 8 страниц, заменив include-фрагментом.

## 8. Критерии готовности

```bash
# хедер только в новом месте
grep -rn "fragments::header" src/main/resources/templates     # пусто
grep -rn "fragments/header/header" src/main/resources/templates --include=*.html | wc -l  # 11

# ui.js подключён на всех страницах с хедером
grep -rln "modules/ui.js\|fragments/index/scripts\|fragments/scripts" \
  src/main/resources/templates --include=*.html

# нет inline-стилей в хедере
grep -n 'style="' src/main/resources/templates/fragments/header/*.html

# нет дублей CSS
grep -rn "mobile-menu" src/main/resources/static/css/layouts/sidebar.css  # пусто/минимум
```

Ручная проверка на 375/768 px: бургер, overlay, close, `Esc`, ссылка, активный пункт; консоль без
ошибок; все 11 страниц.

## 9. Риски и открытые вопросы

1. **`fragments::header` в 11 страницах** — забыть одну = страница без хедера; проверять `grep`.
2. **`servletPath`** зависит от `GlobalController`; при выносе nav параметр должен остаться доступен.
3. **CSRF logout** — не потерять hidden input при дедупликации формы.
4. **CSS-дубли** — удаление `sidebar.css` может изменить z-index/анимацию; проверять визуально.
5. **`!important`** — убирать постепенно, не ломая перекрытие хедера.
6. **scripts-фрагмент**: переименование затронет Index/contact; решить до этапа 5.
7. **`th:utext`/безопасность** — вне этого рефакторинга.

## 10. Открытые вопросы

1. Выносим ли сразу `cta-section`/`footer` из `fragments.html` или только header?
2. Переименовываем `fragments/index/scripts.html` в общий `fragments/scripts.html`?
3. Удаляем `layouts/sidebar.css` полностью или оставляем только не-мобильные стили?
4. Нужен ли focus-trap в сайдбаре (полноценный a11y) или достаточно `aria-expanded`/`Escape`?
