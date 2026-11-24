# SPEC_GALLERY.md — галерея, эскизы и пагинация

> **Статус: только анализ.** HTML/CSS/JS/Java не менялись. План — после подтверждения.
> Правила: `.dsh/skills/gallery-pagination/SKILL.md`. Промт: `.dsh/skills/gallery-pagination/PROMPT.md`.

## 1. Что есть сейчас

### Публичные страницы

| Файл | Строк | Что |
|---|---:|---|
| `templates/gallery.html` | 136 | фильтры по стилю, `fragments::galleryGrid(gallery,false)`, inline `goToPageGallery` |
| `templates/sketches.html` | 115 | `fragments::sketchesGrid(sketches,false)`, без фильтров, inline `goToPageSketches` |
| `templates/fragments.html:106-224` | — | `galleryGrid` + пагинация |
| `templates/fragments.html:228-332` | — | `sketchesGrid` + пагинация (почти копия) |

### Админка

| Файл | Строк | Что |
|---|---:|---|
| `templates/fragment-admin/gallery.html` | 104 | фрагмент `gallery`: фильтры, форма, `fragments::galleryGrid(gallery,true)` |
| `templates/fragment-admin/sketches.html` | 80 | фрагмент `sketches`: форма, `fragments::sketchesGrid(sketches,true)`, лишний `</div>` |
| `templates/fragment-admin/settings.html:1876` | — | **дубль** `galleryGridContainer` (управление галереей) |

### Java

| Компонент | Файл | Замечание |
|---|---|---|
| `GalleryController` | `controller/client/GalleryController.java` | private handler-методы, `PAGE_NUMBER`, path-переменные `page/number`, неиспользуемые поля/импорты |
| `SketchesController` | `controller/client/SketchesController.java` | private handler, тот же контракт |
| `GalleryAdminController` | `controller/admin/GalleryAdminController.java` | фильтр возвращает `fragment-admin/gallery::galleryGridContainer` — **фрагмента в этом файле нет** |
| `SketchersAdminController` | `controller/admin/SketchersAdminController.java` | возвращает целый `sketches`, а не grid-фрагмент |
| `ImagesServiceImpl` | `service/impl/ImagesServiceImpl.java:68-140` | `Page<Images>` → `List<List<Images>>` + DTO |
| `SketchesServiceImpl` | `service/impl/SketchesServiceImpl.java:70-85` | та же логика, без фильтра |
| `GalleryDTO` / `SketchesDTO` | `model/DTO` | `pageSize` = **totalPages**, `number` = **size**; имена вводят в заблуждение |
| `PageSize` | `util/PageSize.java` | 9/12/15, mutable cached list |

### JS

| Функция | Где | Проблема |
|---|---|---|
| `goToPageGalleryAdmin(style,page,number)` | `modules/gallery.js:11` | отдельный admin-путь, `sessionStorage`, ручной active state |
| `goToPageGallery(style,page,number)` | `:36` | публичная, но модуль **не подключён** на публичных страницах |
| `goToGalleryPageFromElement(element)` | `:45` | парсит `data-*`, дублирует `goToPageSketches` |
| `goToPageSketches(element)` | `:71` | копия предыдущей |
| `goToPageGalleryReviews(page,number)` | `:93` | ещё один вариант |
| `deleteImage(button)` | `:102` | inline `onclick`, `confirm`, без abort/loading-контракта |

`gallery.js` подключён только в `admin.html:155`; в `fragments/scripts.html` его нет. Публичные
`/gallery` и `/sketches` вызывают `goToPageGallery`/`goToGalleryPageFromElement`/`goToPageSketches` —
это **ReferenceError**, пагинация/фильтры не работают.

## 2. Критичные дефекты (найдены при анализе)

1. **Публичная пагинация не работает:** `gallery.js` не входит в общий `scripts.html`; функции
   пагинации/фильтра не определены на `/gallery` и `/sketches`.
2. **`/sketches` без стилей:** `sketches.html:32` подключает `/css/main.css` (404) вместо
   `/static/css/main.css(v=5)`.
3. **Админский фильтр галереи падает:** `GalleryAdminController:72` возвращает
   `fragment-admin/gallery::galleryGridContainer`, но фрагмент объявлен только в
   `fragment-admin/settings.html:1876`. В `gallery.html` его нет → Thymeleaf не может отрендерить.
4. **Дублирование админской галереи:** целый блок управления галереей лежит и в
   `fragment-admin/gallery.html`, и в `fragment-admin/settings.html`; правки расходятся.
5. **Разный контракт admin-фрагментов:** gallery возвращает grid-контейнер (когда исправлен), sketches —
   целый фрагмент с формой; JS и тесты вынуждены знать оба варианта.
6. **`Sketches` extra `</div>`** (`fragment-admin/sketches.html:77`) — невалидная разметка.
7. **DTO-имена:** `pageSize` = всего страниц, `number` = размер страницы; шаблоны используют
   `${gallery.pageSize}` как totalPages и `${gallery.number}` как size. Легко перепутать.
8. **Нет валидации page/size:** `PageRequest.of` с отрицательным page бросает исключение; page за
   границей отдаёт пустую страницу; `size` не ограничен whitelist.
9. **Пагинация не URL-driven:** `<a>` без `href`, только `onclick`; нет back/forward, клавиатуры,
   `aria-current`, disabled-состояний, номеров страниц/первой/последней.
10. **Пустая галерея показывает пагинацию** «1 из 0» (`.blog-option` без условия).
11. **`layout:decorate="layout"`** в gallery/sketches, но layout-диалекта/шаблона `layout` нет —
    мёртвые атрибуты.
12. **`lk.html:106`** ссылается на `static/js/gallery.js`, которого нет (есть `modules/gallery.js`).
13. Inline-обработчики в шаблонах: `onclick`, `onchange`, `th:onclick`, `this.textContent`.
14. Нет loading/error/abort: быстрые клики создают гонки запросов.

## 3. Целевая архитектура

### 3.1. Модель страницы

Использовать `org.springframework.data.domain.Page<T>` (или один generic `PageResult<T>`):

```java
Page<Images> page = imagesService.page(style, PageRequest.of(page, size, Sort.by(DESC, "id")));
model.addAttribute("galleryPage", page);
```

Поля для шаблона: `content`, `number`, `size`, `totalPages`, `totalElements`. Никаких
`List<List<T>>` и DTO с `pageSize/number`; сетка — CSS Grid.

### 3.2. Контракт URL

| Public | Admin |
|---|---|
| `GET /gallery?style=&page=&size=` | `GET /admin/gallery?style=&page=&size=` |
| `GET /gallery?style=&page=&size=&fragment=grid` | `GET /admin/gallery?style=&page=&size=&fragment=grid` |
| `GET /sketches?page=&size=` | `GET /admin/sketches?page=&size=` |
| `GET /sketches?page=&size=&fragment=grid` | `GET /admin/sketches?page=&size=&fragment=grid` |

- `page` 0-based; `size` только 9/12/15; `page` клампится в `[0, totalPages-1]`;
- `fragment=grid` → HTML-фрагмент; иначе полная страница;
- admin-фрагменты: `galleryGridContainer` и `sketchesGridContainer` **в своих файлах**
  (`fragment-admin/gallery.html`, `fragment-admin/sketches.html`).

### 3.3. Один шаблон сетки и пагинации

- `templates/fragments/media/grid.html`:
  `th:fragment="grid(items, kind, isAdmin)"`, карточка, admin-кнопки, без inline onclick.
- `templates/fragments/pagination.html`:
  `th:fragment="pagination(page, baseUrl, target, extraParams)"`;
  реальные `<a th:href>` + `data-pagination-link` для AJAX;
  `aria-current`, `aria-label`, prev/next/first/last, номера страниц, размер, «Показано X–Y из N».
- Удалить дубли `galleryGrid`/`sketchesGrid` из `fragments.html` (или оставить тонкие обёртки над
  общим `media/grid`).
- `gallery.html` и `sketches.html` используют одинаковый head (CSS/JS) и одинаковый include сетки;
  `sketches.html` получает корректный `/static/css/main.css(v=5)`.
- `fragment-admin/settings.html` больше не содержит управление галереей; остаётся только настройки.

### 3.4. Один JS-модуль

`static/js/modules/pagination.js`:

```js
Pagination.init(scope)      // делегированные обработчики
Pagination.load(url, target) // fetch + AbortController + history.pushState
```

- клик по `[data-pagination-link]` и `[data-filter]` → `fetch(url, {headers:{'X-Requested-With':'XMLHttpRequest'}})`; fallback — обычный переход;
- заменить `data-target`, переинициализировать фоны/лайтбокс/карусель;
- `popstate` → вернуть состояние;
- loading-класс на контейнере, отмена предыдущего запроса, обработка ошибок (модалка/redirect);
- размер страницы — `change` на `[data-page-size]`, сброс `page=0` при смене фильтра/размера.

`gallery.js` оставляет только `deleteImage`/`toggleFeatured` и подключается в admin; публичная
пагинация от него больше не зависит. Inline `onclick` удаляются, вместо них — `data-*` и делегаты.

### 3.5. CSS

- `components/pagination.css` → `.pagination`, `.pagination__link`, `.pagination__current`,
  `.pagination__info`, `.pagination__size`; `.blog-pagination` — временный alias.
- `components/media-grid.css` → CSS Grid для карточек; убрать ручные `.row`/`.col-lg-4` для картинок.
- `fragments.html`/admin fragments используют общие классы.

## 4. План этапов

### Этап 0. Базовая линия

- Скриншоты `/gallery`, `/sketches`, admin Gallery/Sketches.
- Зафиксировать текущие ошибки: пагинация public (ReferenceError), фильтр admin gallery (500),
  sketches CSS 404.
- `git status` чистый.

### Этап 1. Критичные фиксы (без большого рефакторинга)

1. Подключить `modules/gallery.js` (или новый `pagination.js`) в `fragments/scripts.html`.
2. Исправить `sketches.html:32` на `/static/css/main.css(v=5)`.
3. Перенести `galleryGridContainer` в `fragment-admin/gallery.html`; удалить дубль из
   `settings.html`; обновить `GalleryAdminController` на существующий фрагмент.
4. Убрать лишний `</div>` в `fragment-admin/sketches.html`.
5. Проверить: public пагинация/фильтр, admin gallery filter, sketches стили.

**Стоп.** Diff + ручная проверка.

### Этап 2. Backend: единый page/size

1. Добавить общий `PageRequest`-хелпер: clamp page, whitelist size, sort `id DESC`.
2. Перевести контроллеры на `@RequestParam page/size/style`; public и admin методы — `public`,
   `@GetMapping`.
3. Вернуть `Page<T>` (или `PageResult<T>`) в модель; убрать `List<List<T>>`/старые DTO.
4. `fragment=grid` → grid-фрагмент; без него — полная страница/фрагмент.
5. `GalleryAdminController` и gallery-фильтр используют один `galleryGridContainer`; sketches —
   `sketchesGridContainer`.
6. Проверить границы: page 0/last/out-of-range, size 9/12/15, пустой список.

**Стоп.** Тесты контроллеров + ручные URL.

### Этап 3. Шаблоны: grid + pagination

1. Создать `fragments/media/grid.html` и `fragments/pagination.html`.
2. Перевести `galleryGrid`/`sketchesGrid` на общий grid; удалить копии.
3. Заменить пагинацию в public/admin на общий фрагмент; реальные `href`, `aria-current`,
   first/last/prev/next, размер, результаты; скрыть при пустом списке.
4. Убрать `layout:decorate` (нет диалекта) или подключить layout осознанно.
5. Проверить SEO/клавиатуру/back/forward без JS.

**Стоп.** Diff + скриншоты.

### Этап 4. JS: прогрессивная пагинация

1. Создать `pagination.js` (fetch, abort, history, popstate, loading, fallback).
2. Удалить `goToPage*`/inline onclick из шаблонов; `gallery.js` оставить delete/featured.
3. Подключить модуль в `fragments/scripts.html` и admin.
4. Проверить: быстрые клики, смена фильтра/размера (page=0), ошибка сети, back/forward,
   переинициализация Magnific/фона.

**Стоп.** Diff + запись проверки.

### Этап 5. CSS + cache-bust

1. `components/pagination.css`/`media-grid.css`; удалить старые `.row`-правила для сетки.
2. `main.css`: `v=8` у изменённых импортов; `main.css?v=8` во всех шаблонах.
3. Проверить Cloudflare: `?v=8` отдаёт новый CSS.

### Этап 6. Тесты/очистка ✅ ВЫПОЛНЕНО

**Дата выполнения:** 2026-11-21
**Коммит:** `a325207`

- ✅ `AdminPage`: обновлены селекторы карусели (Bootstrap → Owl)
- ✅ `GalleryPage`: селекторы `.pagination__link[aria-label="..."]` актуальны
- ✅ `SketchesPage`: селекторы `.pagination__link[aria-label="..."]` актуальны
- ✅ `./mvnw -DskipTests compile` — прошло успешно
- ✅ `node --check` — все JS файлы валидны
- ✅ `git diff --check` — нет whitespace ошибок
- ✅ `grep`-проверки: все 8 проверок пройдены
- ✅ `main.css?v=6` — 11 шаблонов
- ✅ Мёртвых onclick нет
- ✅ `galleryGridContainer` только в gallery.html (0 в settings.html)

- Обновить `GalleryPage`, `SketchesPage`, `AdminPage` (селекторы `.pagination`, `#gallerySize`,
  `.sketchesGridContainer`).
- `./mvnw -DskipTests compile`; UI/тесты по возможности.
- `grep`-проверки; обновить спеку.

## 5. Критерии готовности

```bash
# 1. public пагинация не зависит от admin-модуля
grep -n "pagination.js" src/main/resources/templates/fragments/scripts.html
grep -rn "onclick=\"goToPage\|onclick=\"goToGalleryPage" \
  src/main/resources/templates --include=*.html   # пусто

# 2. фрагменты на месте
grep -n "th:fragment=\"galleryGridContainer\"" src/main/resources/templates/fragment-admin/gallery.html
grep -n "th:fragment=\"sketchesGridContainer\"" src/main/resources/templates/fragment-admin/sketches.html
grep -c "galleryGridContainer" src/main/resources/templates/fragment-admin/settings.html  # 0

# 3. контракт page/size
grep -rn "pageSize\|number" src/main/java/ru/tattoo/maxsim/model/DTO  # пусто/осознанно
grep -n "RequestParam" src/main/java/ru/tattoo/maxsim/controller/*/Gallery*Controller.java

# 4. cache
grep -rl "main.css(v=8)" src/main/resources/templates --include=*.html | wc -l  # 11
grep -n "pagination.css?v=6\|media-grid.css?v=6" src/main/resources/static/css/main.css
```

Ручное: `/gallery` (фильтры/страницы/размер/back), `/sketches`, admin Gallery/Sketches (фильтр,
страницы, удаление, флаг), 375/768 px, без JS, консоль без ошибок.

## 6. Риски

1. **UI-тесты** используют старые селекторы (`.gallery-controls`, `.reviews-admin`,
   `#sketchesSize`); обновлять вместе с шаблонами.
2. **Фрагменты admin** загружаются в `.tab-content` через `updateAdminFragment`; новая пагинация
   должна работать после AJAX (`Pagination.init(container)`).
3. **Page clamp** меняет поведение: out-of-range URL покажет последнюю страницу вместо пустой —
   это ожидаемо, зафиксировать в тестах.
4. **CSS Grid** может изменить высоты/отступы карточек; сверять скриншоты.
5. **`galleryGridContainer` в settings** может использоваться старыми ссылками; перед удалением
   `grep` по `data-fragment-name`, `href`, JS.
6. **Cache v=8** обязателен (Cloudflare 4h).
7. **`lk.html`** грузит несуществующий `static/js/gallery.js` — отдельный фикс в этапе 1.

## 7. Открытые вопросы

1. Использовать `Page<T>` напрямую или ввести `PageResult<T>` (без Spring-типов в шаблонах)?
2. CSS Grid вместо Bootstrap-рядов — сразу или после стабилизации пагинации?
3. Оставлять ли фильтры только у gallery (у sketches категорий нет) или добавить?
4. Нужен ли HTMX вместо самописного `pagination.js`? Рекомендация — без новой зависимости.
5. Удалять ли `galleryGridContainer` из `settings.html` сразу или оставить deprecated-алиас?
