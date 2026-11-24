---
name: gallery-pagination
description: Use when refactoring the gallery/sketches pages, their admin fragments, controllers, JavaScript or pagination — unifying public/admin behavior, fixing broken pagination, and moving to URL-driven accessible pagination.
---

# Gallery / Sketches & Pagination Refactor (правила проекта)

## Overview

Галерея и эскизы — две почти одинаковые сущности (картинка + описание + удаление/флаг), но сейчас
у них разные контроллеры, DTO, фрагменты, JS-функции и пагинация. Задача — один контракт:

- один формат страницы (`page`, `size`, `totalPages`, `totalElements`);
- один серверный шаблон сетки и один шаблон пагинации;
- один JS-модуль прогрессивного AJAX;
- одинаковые URL/фрагменты для public и admin.

## Hard rules

### 1. Один контракт пагинации

- Параметры запроса: `page` (0-based), `size` (9/12/15), опционально `style` (только gallery).
- Сервер валидирует: `page = max(0, min(page, totalPages - 1))`, `size` — только из whitelist,
  сортировка стабильная (`id DESC`).
- В модель кладётся `Page<T>` (Spring Data) или `PageResult<T>` с полями:
  `items`, `page`, `size`, `totalPages`, `totalElements`.
- **Запрещены** поля с неоднозначными именами вроде `pageSize = totalPages` и `number = size`
  (текущий `GalleryDTO`/`SketchesDTO`). Шаблоны не должны угадывать смысл поля.
- Нельзя отдавать `List<List<T>>` только ради Bootstrap-колонок: сетка делается CSS Grid, а не
  ручным разбиением на строки.

### 2. Один контракт URL и фрагментов

Public:

```text
GET /gallery?style=&page=&size=       -> полная страница
GET /gallery?style=&page=&size=&fragment=grid -> HTML-фрагмент сетки
GET /sketches?page=&size=             -> полная страница
GET /sketches?page=&size=&fragment=grid -> HTML-фрагмент
```

Admin:

```text
GET /admin/gallery?style=&page=&size=
GET /admin/gallery?style=&page=&size=&fragment=grid
GET /admin/sketches?page=&size=
GET /admin/sketches?page=&size=&fragment=grid
```

- Фрагмент `galleryGridContainer`/`sketchesGridContainer` должен быть **объявлен в том файле, откуда
  его возвращает контроллер** (`fragment-admin/gallery.html` / `fragment-admin/sketches.html`).
- Дублировать управление галереей в `fragment-admin/settings.html` нельзя.
- Контроллерные методы — `public`, `@GetMapping`, без `@PathVariable` для page/size; параметры —
  `@RequestParam`.
- AJAX определяется заголовком `X-Requested-With: XMLHttpRequest` (или `fragment=grid`), а не
  отдельным набором URL на каждый чих.

### 3. Одна сетка и один шаблон пагинации

- `templates/fragments/media/grid.html` — общая карточка/сетка для gallery/sketches, параметры
  `items`, `kind`, `isAdmin`.
- `templates/fragments/pagination.html` — один `pagination(page, baseUrl, target, extraParams)`.
- Пагинация рендерит **настоящие ссылки**:
  ```html
  <a th:href="@{${baseUrl}(page=${page - 1}, size=${size})}"
     data-pagination-link data-target=".galleryFilter">←</a>
  ```
  JS лишь перехватывает клик; без JS ссылки работают как обычные переходы.
- Обязательно: `aria-label="Пагинация"`, `aria-current="page"`, disabled-состояния, номера
  страниц/первая/последняя, «Показано X–Y из N», выбор размера как нормальная форма/ссылки.
- При пустом списке пагинация не показывается (сейчас gallery выводит «1 из 0»).

### 4. Один JS-модуль

- `static/js/modules/pagination.js` — только он управляет пагинацией/фильтрами:
  - делегированный клик по `[data-pagination-link]`;
  - `fetch`/`$.ajax` с `X-Requested-With`, `AbortController` для отмены предыдущего запроса;
  - замена `data-target`, `history.pushState`, обработка `popstate`;
  - loading/disabled состояние, fallback на обычный переход при ошибке;
  - переинициализация фона/лайтбокса/карусели после вставки HTML.
- Модуль подключается и на public, и в admin (сейчас `gallery.js` грузится только в админке —
  публичная пагинация падает с `ReferenceError`).
- Никаких inline `onclick`, `onchange`, `this.textContent` и глобальных `window.goToPage*`.
- `gallery.js` оставляет только админские операции (удаление/флаг); пагинация уходит в
  `pagination.js`.

### 5. Один CSS-компонент

- `components/pagination.css` — классы `.pagination`, `.pagination__link`, `.pagination__current`,
  `.pagination__info`, `.pagination__size`; старые `.blog-pagination` — только как временные алиасы.
- `components/media-grid.css` — сетка карточек (CSS Grid), без `.row`/`.col-lg-4` для картинок.
- После любого изменения CSS — cache-busting: текущая `v=7`, следующая `v=8` (обновить
  `main.css?v=N` во всех шаблонах и `?v=N` у изменённых `@import`).

### 6. Верификация

```bash
# публичная пагинация не должна зависеть от админского модуля
grep -rn "gallery.js\|pagination.js" src/main/resources/templates/fragments/scripts.html
grep -rn "goToPageGallery\|goToPageSketches\|goToGalleryPageFromElement" \
  src/main/resources/templates --include=*.html   # не должно быть inline

# фрагменты, которые возвращает контроллер, существуют там же
grep -n "galleryGridContainer" src/main/resources/templates/fragment-admin/gallery.html
grep -n "sketchesGridContainer" src/main/resources/templates/fragment-admin/sketches.html

# page/size вместо pageSize/number
grep -rn "pageSize\|number" src/main/java/ru/tattoo/maxsim/model/DTO \
  src/main/resources/templates/gallery.html src/main/resources/templates/sketches.html
```

Ручная проверка: `/gallery` (фильтр, страницы, размер, back/forward), `/sketches`, админ-табы
Gallery/Sketches (фильтр, страницы, удаление, флаг), 375/768 px, без JS (обычные ссылки), консоль.

## Common mistakes

- Подключить `gallery.js` только в админке — публичная пагинация молча не работает.
- Вернуть `fragment-admin/gallery::galleryGridContainer`, а фрагмент оставить в `settings.html`.
- Продолжать передавать `pageSize` как totalPages и `number` как size.
- Сделать «пагинацию» из `<a>` без `href` и `onclick` — нет клавиатуры, SEO и back/forward.
- Забыть сбросить `page` при смене фильтра/размера.
- Оставить `layout:decorate="layout"` без layout-диалекта (мёртвый атрибут).
- Забыть актуальную версию (сейчас `v=7`) — Cloudflare отдаст старый CSS.
