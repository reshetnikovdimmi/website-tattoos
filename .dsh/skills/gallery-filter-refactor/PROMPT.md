# PROMPT — gallery filter refactor

Use this prompt in a fresh session. Read `SPEC_GALLERY_FILTER_REFACTOR.md` first.

---

Ты — Java/Thymeleaf/JS-инженер проекта `website-tattoos`. Нужно починить фильтрацию галереи и
привести public/admin к одному контракту, не ломая пагинацию.

## Контекст

- `/gallery` содержит ссылки `.gallery-controls a[data-filter-link]`:
  `/gallery?page=0&size=9&style=<name>`.
- `pagination.js` перехватывает клик и запрашивает `fragment=grid`.
- В `main.js` остался старый обработчик `.gallery-controls ul li`, который меняет только
  `.active`; он не фильтрует.
- `GalleryController`/`GalleryAdminController` дублируют `ALL_GALLERY`, `prepareGalleryData`,
  нормализацию стиля.
- `ImagesService.getGalleryPage(category, principal, page, size)` принимает сырые поля.

## Цель

1. Клик по стилю реально фильтрует public и admin.
2. «Вся галерея» сбрасывает стиль.
3. Один источник нормализации — `GalleryQuery`.
4. Контроллеры тонкие, сервис — constructor injection + read-only transaction.
5. Старый `main.js`-обработчик и `event.which`-гейт удалены.

## Шаги

### 1. JS

- Удалить из `main.js` блок `// Gallery filter`.
- В `pagination.js`:
  - отдельные делегированные обработчики `[data-filter-link]` и `[data-pagination-link]`;
  - не проверять `event.which`; проверять `metaKey/ctrlKey/shiftKey/altKey` и `event.button`;
  - `preventDefault()` только если есть `href` и `data-target`;
  - active-state обновлять в обработчике фильтра.
- `node --check`.

### 2. Backend

- Добавить `GalleryQuery` record:
  - `category()` → null для null/blank/«Вся галерея», иначе `trim()`;
  - `selectedStyle()` для шаблона.
- `ImagesService.getGalleryPage(GalleryQuery, Principal)`.
- `ImagesServiceImpl`: `@RequiredArgsConstructor`, `@Transactional(readOnly = true)`,
  `PageRequestUtils`.
- `GalleryController` и `GalleryAdminController`: только query → service → model/fragment.
- Удалить `prepareGalleryData` и дублирующиеся константы.
- Обновить `LkController`/`ReviewsController`.

### 3. Проверки

```bash
grep -rn "gallery-controls ul li').on" src/main/resources/static/js || true
grep -rn "event.which" src/main/resources/static/js/modules/pagination.js || true
./mvnw -DskipTests compile
./mvnw -DskipTests test-compile
```

Тесты:
- `GalleryQueryTest`: trim, null, «Вся галерея».
- `GalleryPageControllerTest`: `style=  Реализм  ` → `GalleryQuery.category()=Реализм`,
  `fragment=grid` → `gallery::galleryFilter`.

### 4. Ручная проверка

`/gallery`: клик по каждому стилю, «Вся галерея», URL, active, back/forward.
`/admin/gallery`: фильтр, пагинация, удаление, флаг. 375/768 px.

## Stop

После каждого шага — diff + результат проверки. Не менять БД и не добавлять зависимости.
