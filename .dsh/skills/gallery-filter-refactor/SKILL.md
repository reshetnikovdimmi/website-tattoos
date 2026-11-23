# Gallery Filter Refactor

## When to use

When gallery style filters stop changing the grid, active state is wrong, or the
public/admin filter flow needs to be unified with pagination.

## Rules

1. **One owner of filter state.**
   - Server renders `selectedStyle` into `.gallery-controls`.
   - `pagination.js` owns active-state on AJAX clicks.
   - No active-only click handlers in `main.js`.

2. **Filter links are real links.**
   ```html
   <a th:href="@{/gallery(page=0, size=${gallery.size}, style=${currents.name})}"
      data-filter-link data-filter-style="${currents.name}"
      data-target=".galleryFilter">…</a>
   ```

3. **One normalized query.**
   - `GalleryQuery(page, size, style)`.
   - `category()`: null for null/blank/«Вся галерея», otherwise `style.trim()`.
   - Controllers never duplicate style normalization.

4. **Controllers are thin.**
   - Only `@RequestParam` → `GalleryQuery` → service → model/fragment.
   - No `prepareGalleryData`, no duplicate `ALL_GALLERY` constants.

5. **Service is read-only for queries.**
   - Constructor injection.
   - `@Transactional(readOnly = true)` on page reads.
   - Page/size through `PageRequestUtils`; clamp there/one place.

6. **JS delegation.**
   - Separate handlers for `[data-filter-link]` / `[data-pagination-link]`.
   - Do not gate on `event.which`; use modifier keys and `event.button`.
   - Fallback is the real `href` (full page), not a dead `#`.

7. **Fragments stay symmetric.**
   - Public: `gallery::galleryFilter`.
   - Admin: `fragment-admin/gallery::galleryGridContainer`.
   - `fragment=grid` on every AJAX request.

## Verification

```bash
grep -rn "gallery-controls ul li').on" src/main/resources/static/js || true
grep -rn "event.which" src/main/resources/static/js/modules/pagination.js || true
./mvnw -DskipTests compile
./mvnw -Dtest=GalleryQueryTest,GalleryPageControllerTest,PaginationTemplateTest test
```

Manual: style → grid changes; «Вся галерея» resets; active item follows; URL carries `style`;
back/forward works; `/admin/gallery` filter uses `.galleryFragment`.
