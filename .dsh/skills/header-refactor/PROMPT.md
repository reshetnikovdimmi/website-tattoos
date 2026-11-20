# PROMPT_HEADER_REFACTOR — промт для исполнителя

> Скопируй в новый чат/агента. Перед началом прочитай `SPEC_HEADER.md` и
> `.dsh/skills/header-refactor/SKILL.md`.

---

Ты — ведущий Java/Thymeleaf-инженер проекта `website-tattoos`. Нужно завершить рефакторинг глобального
хедера и мобильного меню. P0-фикс уже сделан (см. §5 спеки): не переделывай его, сначала проверь.

## 0. Контекст

- Хедер лежит в `src/main/resources/templates/fragments.html:9-93`, фрагмент `header`; его подключают
  11 страниц через `~{fragments::header}`.
- Внутри — две копии навигации (desktop/mobile), два набора соцсетей/авторизации, 4 inline-стиля.
- `initMobileMenu()` — в `static/js/modules/ui.js` (строки 34-75).
- P0: `modules/ui.js` добавлен на 8 страниц до `main.js`; `initMobileMenu` переписан (toggle/close,
  overlay, `Esc`, клик по ссылке, `aria-expanded`, идемпотентность); в разметке добавлены `aria-*`,
  `id="mobile-menu-sidebar"`, `type="button"`.
- CSS: `layouts/header.css` (482 строки, 1 `!important`) и `layouts/sidebar.css` (161 строка,
  24 `!important`) дублируют 11 селекторов `.mobile-menu-*`/`.mobile-nav*`.

## 1. Цель и границы

**Цель:** один фрагмент хедера, один список навигации, один CSS-владелец, одинаково работающее меню
на всех страницах. Внешний вид не меняется без согласования.

**Не делай:**
- не переписывай дизайн и не меняй пункты меню;
- не трогай контроллеры, кроме явной необходимости (`servletPath` уже есть в `GlobalController`);
- не удаляй CSS без `grep` по всем шаблонам;
- не возвращай `slicknav` и inline-стили;
- не коммить временные изменения `pom.xml`/`skipTests`.

## 2. Подготовка

```bash
git status --short
git switch -c refactor/header   # или согласованное имя
./mvnw -DskipTests compile
node --check src/main/resources/static/js/modules/ui.js
```

Запусти приложение на `:8081` (профиль `local`) и проверь на 375 px:
`/`, `/gallery`, `/sketches`, `/about-me`, `/blog`, `/reviews`, `/contact`, `/login`,
`/registration`, `/lk`, `/admin` — бургер открывает/закрывает меню, `Esc`, клик по фону, клик по
ссылке; консоль без ошибок.

Если какая-то страница не работает — сначала выясни, подключён ли `modules/ui.js` **до** `main.js`
(`grep` по шаблону), и только потом правь JS.

## 3. Этапы

### Этап 1. Вынести хедер из `fragments.html`

1. Создай `templates/fragments/header/header.html` с `th:fragment="header"`; перенеси строки 9-93.
2. Создай `templates/fragments/header/nav.html`:
   ```html
   <ul th:fragment="nav(menuClass)" th:classappend="${menuClass}">
       ... 7 ссылок с th:classappend="${servletPath == '/...' ? 'active':''}" ...
   </ul>
   ```
   Используй его дважды: desktop (`mainmenu`) и mobile (`mobile-nav`). Один список — не копия.
3. Создай `templates/fragments/header/socials.html` (`socials`): WhatsApp, login/logout (POST+CSRF),
   admin/lk. Вызови в desktop и mobile контейнерах.
4. Обнови 11 страниц: замени `~{fragments::header}` на `~{fragments/header/header :: header}`.
5. Удали старый фрагмент из `fragments.html`.
6. Проверь: `grep -rn "fragments::header" templates` пусто; `grep -rn "fragments/header/header"` — 11.

**Стоп.** Покажи diff, список страниц и проверку меню на 2-3 страницах.

### Этап 2. Убрать inline-стили

1. В `header.html` удали `style="display:inline;"` у `.logout-form` — `display:inline` уже есть в
   `components/buttons.css`.
2. Для кнопок logout введи `.logout-btn` (фон/бордер/паддинги) в `components/buttons.css`, удали
   `style="background:none;border:none;padding:0;cursor:pointer;"` (4 места).
3. Проверь внешний вид desktop/mobile и CSRF logout.

**Стоп.** Скриншоты до/после.

### Этап 3. CSS: один владелец

1. Удали из `layouts/sidebar.css` дубли `.mobile-menu-*`/`.mobile-nav*` (11 селекторов).
2. После удаления убери `!important` из оставшихся правил (24 → 0, если файл не нужен — удали его и
   импорт из `main.css`).
3. Удали дубль `body.menu-open` (оставь в `header.css`).
4. Проверь z-index/анимацию сайдбара и хедера на 375/768 px.
5. Адаптивные правила бургер-меню из `responsetive/tablet.css:222-245` и `mobile.css:89-100,203-215`
   перенеси в `header.css` (или оставь, но без дублей).

**Стоп.** Покажи, какие селекторы удалены и результаты проверки.

### Этап 4. Доступность и поведение

1. При открытии — фокус на `.mobile-menu-close`; при закрытии — назад на `.mobile-menu-toggle-icon`.
2. `aria-hidden`/`inert` на сайдбаре в закрытом состоянии (по поддержке).
3. Проверь: открытие, закрытие, `Esc`, overlay, клик по ссылке, повторное открытие, отсутствие
   «залипшего» `body.menu-open`.
4. Консоль без ошибок на всех 11 страницах.

**Стоп.** Отчёт по чек-листу.

### Этап 5. Единый scripts-фрагмент

1. Переименуй `templates/fragments/index/scripts.html` → `templates/fragments/scripts.html`
   (фрагмент `scripts`), обнови `Index.html`, `contact.html`.
2. Замени копипасту `<script ... ui.js>` на include фрагмента на 8 страницах.
3. Убедись, что порядок: vendor → modules (ui/video/image) → `main.js`.

**Стоп.** Покажи `grep`-проверку и diff.

## 4. Финальная проверка

```bash
grep -rn "fragments::header" src/main/resources/templates          # пусто
grep -rn "fragments/header/header" src/main/resources/templates --include=*.html | wc -l  # 11
grep -n 'style="' src/main/resources/templates/fragments/header/*.html  # пусто
grep -rn "mobile-menu" src/main/resources/static/css/layouts/sidebar.css  # пусто
node --check src/main/resources/static/js/modules/ui.js
./mvnw -DskipTests compile
git diff --check
```

Плюс ручная проверка 11 страниц на 375/768 px: открыть/закрыть, `Esc`, overlay, ссылка, активный
пункт, консоль.

## 5. Формат отчёта после каждого этапа

1. Что изменено (файлы).
2. Вывод проверок (grep/compile/консоль/скриншоты).
3. Что осталось/риски.
4. Коммит (хеш, сообщение) — если владелец разрешил.
5. Вопросы.

Не переходи к следующему этапу без явного «продолжай».
