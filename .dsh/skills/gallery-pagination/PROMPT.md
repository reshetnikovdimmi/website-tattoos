# PROMPT_GALLERY_REFACTOR — промт для исполнителя

> Скопируй в новый чат/агента. Сначала прочитай `SPEC_GALLERY.md` и
> `.dsh/skills/gallery-pagination/SKILL.md`.

---

Ты — ведущий Java/Thymeleaf/JS-инженер проекта `website-tattoos`. Нужно привести галерею и эскизы
к одному контракту: общие контроллерные параметры, одна сетка, одна пагинация, один JS-модуль,
одинаково работающие public и admin.

## 0. Контекст (подтверждённые факты)

- Public: `gallery.html` (фильтры + `fragments::galleryGrid`), `sketches.html` (только grid).
- Admin: `fragment-admin/gallery.html`, `fragment-admin/sketches.html`; в `fragment-admin/settings.html`
  лежит **дубль** `galleryGridContainer`.
- `gallery.js` подключён **только** в admin, поэтому public-пагинация падает (`goToPageGallery`
  не определена).
- `sketches.html:32` подключает `/css/main.css` (404) вместо `/static/css/main.css`.
- `GalleryAdminController` возвращает `fragment-admin/gallery::galleryGridContainer`, но фрагмент
  объявлен в `settings.html`, а не в `gallery.html`.
- `GalleryDTO`/`SketchesDTO`: `pageSize` = totalPages, `number` = size; шаблоны используют именно так.
- Inline `onclick`, глобальные `goToPage*`, отсутствие URL/back-forward/aria.

**Ничего не переписывай вслепую:** сначала проверь `git status` и воспроизведи баги на текущем коде.

## 1. Цель и границы

**Цель:** `page`/`size` как query-параметры, `Page<T>`/`PageResult<T>`, общий
`fragments/media/grid.html` + `fragments/pagination.html`, `pagination.js` с прогрессивным AJAX.
Public и admin ведут себя одинаково, но admin дополнительно показывает формы/удаление/флаг.

**Не делать:**
- не менять БД/модели сущностей без необходимости;
- не добавлять HTMX/новые библиотеки;
- не удалять старые URL/классы, пока UI-тесты не обновлены;
- не трогать несвязанные разделы.

## 2. Подготовка

```bash
git status --short
git switch -c refactor/gallery-pagination
./mvnw -DskipTests compile
```

Скриншоты «до»: `/gallery`, `/sketches`, `admin → Gallery`, `admin → Sketches`.
Проверь консоль: ReferenceError на public, 404 на sketches CSS, ошибку admin-фильтра.

## 3. Этапы (после каждого — стоп и отчёт)

### Этап 1. Критичные фиксы (маленький коммит)

1. Подключить `modules/gallery.js` (или временно новый `pagination.js`) в
   `templates/fragments/scripts.html`, чтобы public-пагинация работала.
2. В `sketches.html` заменить `/css/main.css` на `/static/css/main.css(v=5)`.
3. Перенести `galleryGridContainer` из `fragment-admin/settings.html` в
   `fragment-admin/gallery.html` (рядом с `gallery`), удалить дубль из settings; убедиться, что
   `GalleryAdminController` возвращает существующий фрагмент.
4. Убрать лишний `</div>` в `fragment-admin/sketches.html:77`.
5. Проверить: public фильтр/страницы, admin gallery filter, sketches стили, консоль.

Коммит: `fix(gallery): restore public pagination and admin fragments`.

**Стоп.** Diff + проверка.

### Этап 2. Backend: page/size

1. Ввести общий хелпер (`PageRequestUtils`/`PageParams`):
   - `page = max(0, min(page, totalPages - 1))`;
   - `size` только 9/12/15 (иначе 9);
   - `Sort.by(DESC, "id")`.
2. Перевести `GalleryController`, `SketchesController`, `GalleryAdminController`,
   `SketchersAdminController` на `public @GetMapping` + `@RequestParam page/size/style`
   (никаких path-переменных для page/size, никаких private handler-методов).
3. Вернуть `Page<T>` (или один generic `PageResult<T>`) в модель; убрать `List<List<T>>` и
   `GalleryDTO`/`SketchesDTO` с `pageSize/number`.
4. `fragment=grid` → grid-фрагмент; без него — полная страница/фрагмент.
5. Проверить границы: page 0/last/out-of-range, size 9/12/15, пустой список.

Коммит: `refactor(gallery): unify page/size contract`.

**Стоп.** Тесты/ручные URL + отчёт.

### Этап 3. Шаблоны

1. Создать `templates/fragments/media/grid.html`:
   `grid(items, kind, isAdmin)` — карточка, admin-кнопки, без inline.
2. Создать `templates/fragments/pagination.html`:
   `pagination(page, baseUrl, target, extraParams)` — реальные `<a href>`, `aria-current`,
   prev/next/first/last, номера, размер, «Показано X–Y из N»; скрывать при пустом списке.
3. Перевести gallery/sketches grids на общий фрагмент; удалить копии из `fragments.html`.
4. Обновить `gallery.html`, `sketches.html`, admin fragments; убрать `layout:decorate` (нет
   диалекта) или подключить layout осознанно.
5. Public/admin: одинаковые include/классы; `sketches.html` — корректный CSS.

Коммит: `refactor(gallery): share grid and pagination templates`.

**Стоп.** Скриншоты + без-JS проверка.

### Этап 4. JS

1. Создать `static/js/modules/pagination.js`:
   - делегированный клик по `[data-pagination-link]`, `[data-filter]`, `[data-page-size]`;
   - `fetch` + `X-Requested-With: XMLHttpRequest`, `AbortController`;
   - замена `data-target`, `history.pushState`, `popstate`;
   - loading/disabled, fallback на обычный переход, переинициализация фонов/лайтбокса;
   - смена фильтра/размера → `page=0`.
2. Удалить `goToPage*` и inline `onclick` из шаблонов; `gallery.js` оставить delete/featured.
3. Подключить `pagination.js` и в public, и в admin.
4. Проверить: быстрые клики, back/forward, ошибка сети, смена фильтра/размера, пустой список.

Коммит: `refactor(gallery): progressive pagination js`.

**Стоп.** Diff + запись проверки.

### Этап 5. CSS + cache

1. `components/pagination.css` — новые `.pagination*`; `.blog-pagination` — alias.
2. `components/media-grid.css` — CSS Grid; убрать `.row/col` для картинок.
3. `main.css`: `?v=6` у изменённых импортов; `main.css?v=6` в 10 шаблонах.
4. Проверить production URL с `?v=6`.

Коммит: `refactor(gallery): modern pagination styles`.

**Стоп.** Скриншоты.

### Этап 6. Тесты/очистка

1. Обновить `GalleryPage`, `SketchesPage`, `AdminPage` (селекторы пагинации/размера).
2. `./mvnw -DskipTests compile`; при возможности — UI/E2E.
3. `grep`-проверки; удалить мёртвый код/дубли; обновить спеку.

## 4. Проверки

```bash
grep -n "pagination.js" src/main/resources/templates/fragments/scripts.html
grep -rn "onclick=\"goToPage\|onclick=\"goToGalleryPage" src/main/resources/templates --include=*.html
grep -n "th:fragment=\"galleryGridContainer\"" src/main/resources/templates/fragment-admin/gallery.html
grep -n "th:fragment=\"sketchesGridContainer\"" src/main/resources/templates/fragment-admin/sketches.html
grep -c "galleryGridContainer" src/main/resources/templates/fragment-admin/settings.html
grep -rl "main.css(v=6)" src/main/resources/templates --include=*.html | wc -l
node --check src/main/resources/static/js/modules/pagination.js
./mvnw -DskipTests compile
git diff --check
```

Ручное: `/gallery` (фильтр/страницы/размер/back), `/sketches`, admin Gallery/Sketches (фильтр,
пагинация, удаление, флаг), 375/768 px, без JS, консоль без ошибок.

## 5. Формат отчёта

1. Что изменено (файлы).
2. Вывод проверок (grep/compile/консоль/скриншоты).
3. Риски/что осталось.
4. Коммит (хеш, сообщение).
5. Вопросы.

Не переходи к следующему этапу без явного «продолжай».
