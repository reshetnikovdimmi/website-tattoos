# SPEC_INDEX.md — рефакторинг главной страницы и стилей

> **Статус: только анализ.** HTML, CSS, JS не изменялись. Ниже — инвентарь, целевая структура и план.
> Выполнять только после подтверждения. Правила: `.dsh/skills/index-refactor/SKILL.md`.
> Промт для исполнителя: `.dsh/skills/index-refactor/PROMPT.md`.

## 0. Как собирались данные

- `code_index`/`code_search` индексируют только Java/JS; HTML-шаблоны в индекс не попадают.
  По `templates` работали `grep`/чтением, по JS — `code_search`/`code_refs`.
- Проверены: `Index.html` (703 строки), `main.css`, `pages/*.css`, `responsetive/*.css`,
  `modules/*.js`, `main.js`, история удаления `style.css` (`git show 3d9a573`).

## 1. Инвентарь Index.html

703 строки; 3 `th:fragment`, 4 `th:replace`, 23 `th:each`, 15 `data-setbg`, 4 inline `style=`,
2 inline `<script>`.

| # | Блок | Строки | Корневой класс | CSS-файл | Данные/циклы | Замечания |
|---|---|---|---|---|---|---|
| 0 | preloader | 73-77 | `#preloder` | — (стилей нет) | — | 4 строки; CSS для него отсутствует — решить при переносе |
| 1 | header | 78 | — | `layouts/header.css` | `fragments::header` | include |
| 2 | hero | 81-121 | `hero-section` | `pages/hero.css` | `carousel[0].banners` | `data-setbg` у слайдов |
| 3 | features | 122-165 | `feature-section` | `pages/feature.css` | `carousel[0].feature` | пустое состояние 139-165 |
| 4 | about | 166-213 | `home-about spad` | `pages/about.css` | `th:with aboutList` | пустое состояние 192-213 |
| 5 | classes | 214-288 | `classes-section` | `pages/classes.css` | `th:each current : carousel` | два контейнера: `class-title` и `container-fluid`; пустое состояние |
| 6 | price | 291-322 | `price-section spad set-bg` | `pages/price.css` | `th:each current : carousel` | `data-setbg="img/price-bg.jpg"` |
| 7 | chooseus | 323-374 | `chooseus-section spad` | `pages/chooseus.css` | `th:each current : carousel` | пустое состояние 358-374 |
| 8 | video | 375-425 | `video-section spad` | `pages/video.css` | `th:each current : carousel` | карточки + owl; модалка подключается отдельно |
| 9 | video modal | 426 | — | `fragments/modals/video.html` | include | уже вынесена |
| 10 | reviews | 429-591 | `blog-section spad` | `pages/blog-section.css` | `reviewsLimit`, `carousel.review` | masonry-сетка, 4 состояния |
| 11 | cta | 592 | — | `layouts/cta-section.css` | `fragments::cta-section` | include |
| 12 | map/contact | 596-663 | `map-section` | `pages/map-section.css` | `th:each current : contact` | `map-widget` (602) и `contact-form` (636) — **используются и в contact.html** |
| 13 | footer | 664 | — | `layouts/footer.css` | `fragments::footer` | include |
| 14 | scripts/analytics | 670-698 | — | — | — | inline Metrika, vendor scripts |

### Фрагменты внутри Index

| Фрагмент | Строки | Используется |
|---|---|---|
| `verificationMetaTags` | 13-23 | только в Index (по `grep`); можно вынести в `fragments/index/verification-meta.html` |
| `map-widget` | 602-634 | `contact.html:60` — `~{Index::map-widget}` |
| `contact-form` | 636-663 | `contact.html:85` — `~{Index::contact-form}` |

**Следствие:** перенос `map-widget`/`contact-form` в `fragments/index/` требует правки `contact.html`,
поэтому целевое место для них — `templates/fragments/contact/` (общие для двух страниц).

## 2. P0: JS на главной не инициализируется

`main.js` — 57 строк, «точка входа». В `$(document).ready` (строки 46-57) вызывает:

```js
initStickyHeader(); initBackgrounds(); initMobileMenu(); initOwlCarousels();
initMagnificPopup(); initNiceSelect(); initTimetableFilter(); initVideoCarousel();
```

Но определения лежат в модулях:

- `modules/ui.js` — `initStickyHeader`, `initBackgrounds`, `initMobileMenu`, `initOwlCarousels`,
  `initMagnificPopup`, `initNiceSelect`, `initTimetableFilter` (экспорт в конце файла);
- `modules/video.js` — `initVideoCarousel`, `initVideoModal` (авто-инициализация на `ready`);
- `modules/image.js` — `sendMail` (строка 188), которую вызывает форма Index (`:639`).

**Модули подключаются только в `admin.html:152-160`.** Index подключает только `main.js` (`:678`).
Итог на главной:

- `ReferenceError: initStickyHeader is not defined` на `ready` — обрывается вся цепочка инициализации;
- не применяются `data-setbg` (15 фонов), не работают owl-карусели, sticky/mobile menu, magnific,
  nice-select, видео-карусель и видео-модалка;
- `sendMail` не определён → `th:onsubmit="sendMail(...)"` на форме падает, AJAX-отправки нет.

Та же проблема у `contact.html` (подключает только `main.js`), но в рамках этой спеки чиним Index;
для contact — обязательная проверка после переноса общих фрагментов и отдельный тикет на такой же
фикс JS (или делаем сразу, если владелец разрешит).

## 3. P0/P1: CSS — сломанные импорты и потерянные утилиты

### 3.1. `main.css`

- Кастомные стили импортируются **до** Bootstrap: `components/pages` — строки 10-47, а
  `bootstrap.min.css`, `font-awesome`, `owl` — строки 56-58. При равной специфичности Bootstrap
  перекрывает кастомные стили. Правильный порядок: vendor → base → components → layouts → pages →
  responsive.
- `pages/user.css` импортируется (`main.css:32`), но файла **нет**; есть пустой
  `pages/useer.css` (0 байт). Браузер молча игнорирует битый `@import`.
- Bootstrap/font-awesome/owl подключены и в `<head>` Index (`:42-49`), и через `main.css` — двойная
  загрузка CSS.
- `pages/admin-common.css` (47 КБ), `pages/mail.css` (26 КБ), `pages/backup.css`, `pages/lk-wrapper.css`
  (25 КБ), `pages/login.css` грузятся на всех страницах, включая главную. При этом `.admin-controls`
  из `admin-common.css` нужен публичным `fragments.html:201,322` — просто убрать нельзя, нужно
  разделение shared/admin.

### 3.2. Потерянные базовые классы (регресс от `3d9a573`)

Коммит `3d9a573` удалил `static/css/style.css` как «неиспользуемый Activitar template», но в нём
остались правила, которые до сих пор используются на главной:

| Селектор | Старое место | Сейчас |
|---|---|---|
| `.section-title h2` (48px, #fff, uppercase), `.section-title p` | `style.css:133-144` | **нет нигде**; остались только responsive-overrides в `tablet.css`/`mobile.css` |
| `.set-bg` (no-repeat, cover, top center) | `style.css:146-150` | **нет** |
| `.spad` (padding 100/100), `.spad-2` (50/70) | `style.css:152-160` | **нет** |
| `.primary-btn` (inline-block, Oswald, 14px, width 160px, line-height 45px, градиент), `.primary-btn:after` | `style.css:177-230` | **нет** |
| `.empty-hint`, `.empty-class-item`, `.empty-class-content` | — | **нет** (используются в Index `:241,277-278`) |

Проверка: `grep -rn "^\.primary-btn\|^\.section-title\|^\.set-bg\|^\.spad" src/main/resources/static/css`
пусто. Это объясняет «поехавшую» главную и должно быть восстановлено из
`git show 3d9a573^:src/main/resources/static/css/style.css` (или переписано на переменные).

`.short-details` и `.long-details` не имеют базовых правил даже в старом файле — только responsive
переопределения. Нужно решение: оставить как есть или задать компонент.

### 3.3. Текущие стили секций

| Файл | Строк | `!important` | hex-цветов | Замечание |
|---|---:|---:|---:|---|
| `pages/hero.css` | 217 | 0 | 7 | |
| `pages/feature.css` | 177 | 0 | 6 | |
| `pages/about.css` | 148 | 0 | 6 | |
| `pages/classes.css` | 224 | 0 | 7 | |
| `pages/price.css` | 226 | 0 | 11 | |
| `pages/chooseus.css` | 195 | 0 | 6 | |
| `pages/video.css` | 594 | 7 (`:140-150`) | 38 | публичная часть 1-380 + админ-часть 381-591 + responsive 592+ |
| `pages/blog-section.css` | 424 | 0 | 12 | |
| `pages/map-section.css` | 381 | 2 (`:36-37`) | 21 | iframe width/height `!important` |

- Дубли селекторов между секциями: только `.instruction-note` и `.instruction-note i` в
  `video.css` и `map-section.css`.
- Пересечение `map-section.css` ↔ `contact-map.css`: 4 селектора (`.map-widget iframe`,
  `.map-widget:hover`, `.status-message.error`, `.step-example code`).
- `blog-section.css` ↔ `blog.css` — 0 общих; `about.css` ↔ `about-us.css` — 0 общих. Это разные
  страницы, не дубли.
- Адаптив для Index размазан по `responsetive/{desktop,tablet,mobile}.css` (35 КБ / 17 КБ / 2.7 КБ)
  вместе с другими страницами.

### 3.4. Инлайн-стили и сторонний код

| Место | Что | Куда |
|---|---|---|
| `Index.html:66,696` | `style="position:absolute; left:-9999px"` (Metrika noscript) | `.metrika-pixel` в `components/utilities.css` |
| `Index.html:648` | inline-стили honeypot-поля | `.visually-hidden` |
| `Index.html:651` | `style="display:none"` у `.status-message` | `.status-message` уже есть в `pages/map-section.css:164`; добавить `display:none` + `.is-visible` |
| `Index.html:56-65` | inline Metrika (ID 112720696) | `fragments/index/analytics-head.html` |
| `Index.html:681-694` | inline Metrika (ID 98027672) | `fragments/index/analytics-body.html` |
| `Index.html:51-53` | VK video + Yandex ads | `analytics-head.html` |
| `Index.html:639` | `th:onsubmit="sendMail(...)"` | `data-form="contact"` + module listener |
| `Index.html` 15 шт. | `data-setbg` | **оставить**: это data-атрибут, JS `initBackgrounds()` ставит фон |

Два разных счётчика Metrika (112720696 в head, 98027672 в body) — вероятно, наследие; удаление
меняет аналитику, нужно решение владельца.

## 4. Целевая структура

```
templates/
├── Index.html                          # композиция, ~120-150 строк
└── fragments/
    ├── index/
    │   ├── preloader.html              # preloader
    │   ├── hero.html                   # фрагмент hero
    │   ├── features.html               # фрагмент features
    │   ├── about.html                  # фрагмент about
    │   ├── classes.html                # фрагмент classes
    │   ├── price.html                  # фрагмент price
    │   ├── chooseus.html               # фрагмент chooseus
    │   ├── video.html                  # фрагмент video (карусель/карточки)
    │   ├── reviews.html                # фрагмент reviews
    │   ├── map.html                    # фрагмент map (секция; включает contact/*)
    │   ├── analytics-head.html         # Metrika head + VK + ads
    │   ├── analytics-body.html         # Metrika body
    │   └── scripts.html                # vendor + modules + main.js
    └── contact/
        ├── map-widget.html             # общий map-widget
        └── contact-form.html           # общий contact-form
```

| Фрагмент | Источник | Обязательные переменные модели |
|---|---|---|
| `hero` | Index 81-121 | `carousel` |
| `features` | Index 122-165 | `carousel` |
| `about` | Index 166-213 | `carousel` |
| `classes` | Index 214-288 | `carousel` |
| `price` | Index 291-322 | `carousel` |
| `chooseus` | Index 323-374 | `carousel` |
| `video` | Index 375-425 | `carousel` |
| `reviews` | Index 429-591 | `reviewsLimit`, `carousel` |
| `map` | Index 596-663 | `contact`, `details`, `status` |
| `map-widget` | Index 602-634 | `current.mapIframe` (из `contact` в page) |
| `contact-form` | Index 636-663 | `details`, `status` |

Index после рефакторинга: head/SEO + `analytics-head` + preloader + header + 9 секций + cta + map +
footer + `scripts` + `analytics-body`.

## 5. План по этапам (после подтверждения)

> Рекомендуется ветка `refactor/index-page`. Один этап = один коммит + стоп-отчёт.

### Этап 0. Базовая линия
- Зафиксировать скриншоты `/` (desktop/tablet/mobile) и консоль браузера «до».
- `git status` — рабочее дерево должно быть чистым (или изменения пользователя закоммичены).
- Проверить, что приложение поднимается; зафиксировать текущие ошибки консоли (ожидаем
  `ReferenceError`).

### Этап 1. P0 JS: оживить главную
1. Создать `fragments/index/scripts.html` (или добавить в Index) с порядком:
   jQuery → bootstrap → magnific → mixitup → nice-select → slicknav → owl → masonry →
   `modules/ui.js` → `modules/video.js` → `modules/image.js` → `main.js`.
2. Убедиться, что `main.js` вызывает только подключённые функции; при необходимости добавить
   guard `typeof fn === 'function'`.
3. Проверить: `data-setbg` применяются, hero/видео-карусели крутятся, меню/модалка работают,
   форма `sendMail` отвечает без `ReferenceError`.
4. Проверить `/contact` (та же схема скриптов — решить, чинить ли в этом же этапе).

### Этап 2. P0 CSS: восстановить утилиты и порядок
1. В `components/buttons.css` вернуть `.primary-btn` и `:after` из
   `git show 3d9a573^:.../style.css:177-230`, переведя цвета на `var(--color-*)`.
2. Создать `components/section-title.css` с `.section-title h2/p` (старые 133-144, цвета → переменные).
3. Создать `components/utilities.css`: `.set-bg`, `.spad`, `.spad-2`, `.visually-hidden`,
   `.metrika-pixel`.
4. `.status-message` уже определён в `pages/map-section.css:164` — добавить туда `display:none` и
   `.status-message.is-visible` (не создавать дубль).
5. Добавить empty-state классы (`.empty-hint`, `.empty-class-item`, `.empty-class-content`) в
   `pages/classes.css` (или общий `components/empty-state.css`).
6. `main.css`: убрать `pages/user.css` + удалить пустой `pages/useer.css`; убрать дублирующие
   импорты vendor (vendor уже в `<head>`); порядок base → components → layouts → pages → responsive.
7. Проверить визуально: заголовки, кнопки, отступы секций, фоны.

### Этап 3. Структура: Index → композиция
1. Вынести секции по одной (hero → features → about → classes → price → chooseus → video →
   reviews), после каждой — проверка страницы.
2. Общие `map-widget`/`contact-form` — в `fragments/contact/`; обновить `Index.html` и `contact.html`
   (`:60`, `:85`).
3. `map`-секцию — в `fragments/index/map.html`, внутри — include общих фрагментов.
4. `verificationMetaTags` — в `fragments/index/verification-meta.html` (или оставить в head).
5. Index сократить до композиции; проверить все 23 `th:each`, пустые состояния, `reviewsLimit`.
6. Обновить ссылки в `contact.html`; `grep -rn "Index::" templates` должен быть пуст.

### Этап 4. Инлайн-код
1. `analytics-head.html` / `analytics-body.html`: перенести Metrika/VK/ads; решить судьбу двух ID.
2. Honeypot → `.visually-hidden`; Metrika noscript → `.metrika-pixel`.
3. `.status-message` — CSS-класс; `image.js` переключить с `.show()/.hide()` на
   `.classList.toggle('is-visible')` (или оставить jQuery, но без inline display).
4. `th:onsubmit` → `data-form="contact"` + делегированный listener в модуле.
5. Проверить отправку формы и сообщения об ошибках.

### Этап 5. CSS ownership/perf (после стабилизации)
1. `pages/video.css` разделить: публичная часть 1-380 и админ-часть 381-591; админ-файл подключать
   только в `admin.html` (или в admin-фрагменте).
2. Вынести admin-only CSS (`admin-common.css`, `mail.css`, `backup.css`, `lk-wrapper.css`,
   `login.css`) из `main.css` на соответствующие страницы; shared `.admin-controls` — в
   `components/`.
3. Удалить `!important` в `video.css:140-150` и `map-section.css:36-37`, повысив специфичность.
4. Дубли `.instruction-note` (video/map) — вынести в `components/`.
5. Заменить hex-цвета на переменные в 9 файлах секций (постепенно, по файлу).
6. Решить политику адаптива: новые правила — в файле секции, старые — отдельным этапом.

### Этап 6. Верификация и документация
- `grep`-проверки (раздел 6).
- `./mvnw -DskipTests compile`; тесты (помнить: `pom.xml` содержит `<skipTests>true</skipTests>`,
  для прогона нужно временно переключить/использовать профиль — не коммитить переключение).
- Ручные страницы: `/`, `/contact`, `/admin`, `/lk`, `/reviews`; консоль без ошибок.
- Обновить `SPEC_INDEX.md` (статус этапов) и `docs/` при необходимости.

## 6. Критерии готовности

```bash
# 1. Index — композиция, без секционной разметки (эвристика: <section> нет)
grep -n "<section" src/main/resources/templates/Index.html

# 2. Нет inline-стилей/скриптов в Index и секциях
grep -rn 'style=\|<style' src/main/resources/templates/Index.html \
  src/main/resources/templates/fragments/index

# 3. Модули подключены до main.js
grep -n "modules/ui.js\|modules/video.js\|modules/image.js\|main.js" \
  src/main/resources/templates/fragments/index/scripts.html

# 4. Нет битых CSS-импортов
while read -r p; do test -f "src/main/resources/static/css/${p}" || echo "НЕТ: $p"; done \
  < <(grep -oP "@import '\K[^']+" src/main/resources/static/css/main.css)

# 5. Утилиты восстановлены
grep -rn "^\.primary-btn\|^\.section-title\|^\.set-bg\|^\.spad" \
  src/main/resources/static/css/components src/main/resources/static/css/base

# 6. Нет старых include-путей
grep -rn "Index::" src/main/resources/templates
```

Плюс: нет `ReferenceError` в консоли; форма отправляется; все секции визуально совпадают со
скриншотами «до».

## 7. Риски

1. **Контекст Thymeleaf.** Фрагменты используют переменные страницы (`carousel`, `contact`,
   `reviewsLimit`, `details`, `status`, `setting`, `yandexMeta`, `googleMeta`). При выносе легко
   потерять `th:with`/`th:each`-переменную; проверять каждую секцию.
2. **contact.html** зависит от `Index::map-widget`/`Index::contact-form` — забыть обновить include
   значит сломать страницу контактов.
3. **UI-тесты/селекторы.** Selenium `AdminPage`, E2E-тесты и формы завязаны на классы/ids; менять
   разметку без проверки нельзя.
4. **Порядок CSS.** Перестановка vendor/кастомных стилей может изменить внешний вид (Bootstrap
   начнёт перекрывать иначе). Нужны скриншоты до/после.
5. **Повторное удаление нужного CSS.** Урок `style.css`: перед удалением селектора — `grep` по всем
   шаблонам, включая `fragments/*` и `fragment-admin/*`.
6. **`data-setbg`** работает только после `initBackgrounds()`; если модули не подключены — фон
   пропадает. Это и есть текущий P0.
7. **Две Metrika** — удаление одного счётчика меняет аналитику; только по решению владельца.
8. **`pom.xml` skipTests=true** — тесты молча не запускаются; для проверки нужно временное
   переключение (не коммитить).
9. **`th:utext` mapIframe** (`Index:604`) — потенциальный XSS из админского поля; не расширять
   этот рефакторинг на безопасность без отдельного решения.

## 8. Открытые вопросы (решить до этапов 3-5)

1. **BEM:** переименовывать ли существующие классы (`hero-section` → `index-hero`)? Рекомендация:
   структурный этап — без переименований; BEM — только для новых компонентов, полный ренейм отдельно.
2. **Metrika:** два счётчика (112720696 и 98027672) — оставить оба, удалить один или уточнить у
   владельца?
3. **Admin CSS:** выносить ли `admin-common/mail/backup/lk-wrapper/login` из общего бандла сразу или
   отдельным этапом после главной?
4. **`.section-title`:** восстановить точные legacy-стили (48px, uppercase) или сделать новый
   компонент по дизайну?
5. **contact.html JS:** чинить подключение модулей на контактах в этом же рефакторинге или отдельным
   тикетом?
6. **`th:utext` mapIframe:** закрывать XSS сейчас или отдельной задачей безопасности?
7. **`data-setbg`:** оставляем как исключение (data-атрибут + JS) или переводим на `<img>`/CSS-классы?
