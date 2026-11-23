# SPEC — фильтрация галереи и рефакторинг GalleryQuery

## 1. Симптом

На `/gallery` клик по стилю («Реализм», «Олдскул», «Вся галерея») перестал менять сетку.
Пагинация при этом работает. В `main.js` остался старый обработчик:

```js
$('.gallery-controls ul li').on('click', function() {
  $('.gallery-controls ul li').removeClass('active');
  $(this).addClass('active');
});
```

Он меняет только CSS-класс, но не делает запрос. Он появился до перехода на ссылки
`/gallery?style=...&fragment=grid` и теперь дублирует `pagination.js`.

## 2. Корневые причины

1. **Двойная ответственность за active-state.** `main.js` и `pagination.js` одновременно
   управляют `.active`; старый обработчик не инициирует фильтрацию.
2. **Слишком строгая проверка клика.** В `pagination.js` было:
   `if (event.which !== 1 || ...) return;`. Для клавиатуры/некоторых браузеров `which`
   может быть `0`/`undefined`, ссылка не перехватывается.
3. **Стиль не нормализовался.** Старый JS делал `style.trim()` перед URL. Новый шаблон
   передавал `currents.name` как есть; при пробелах в БД фильтр не находил категорию.
4. **Дублирование в контроллерах.** `GalleryController` и `GalleryAdminController` имели
   собственные `ALL_GALLERY`, `DEFAULT_PAGE/SIZE`, `prepareGalleryData` и по-разному
   нормализовали стиль.
5. **Сервис принимал сырые `category/page/size`.** `ImagesService.getGalleryPage` смешивал
   нормализацию, пагинацию и фильтр; не было `GalleryQuery`, `@Transactional(readOnly)`,
   constructor injection.

## 3. Целевой контракт

```text
GET /gallery?style=<name>&page=0&size=9&fragment=grid
GET /admin/gallery?style=<name>&page=0&size=9&fragment=grid
```

- `style` = null/пусто/«Вся галерея» → без фильтра.
- `style` обрезается по краям; пустой после trim → без фильтра.
- `page` 0-based, `size` 9/12/15, клампинг — в сервисе.
- `fragment=grid` → только grid-фрагмент; без него — полная страница.

## 4. Что изменено

### Backend

- Новый [GalleryQuery.java](src/main/java/ru/tattoo/maxsim/model/DTO/GalleryQuery.java):
  `page`, `size`, `style`; `category()` и `selectedStyle()`.
- [ImagesService.java](src/main/java/ru/tattoo/maxsim/service/interf/ImagesService.java):
  `getGalleryPage(GalleryQuery query, Principal principal)`.
- [ImagesServiceImpl.java](src/main/java/ru/tattoo/maxsim/service/impl/ImagesServiceImpl.java):
  constructor injection (`@RequiredArgsConstructor`), `@Transactional(readOnly = true)`,
  единая нормализация через `GalleryQuery`, один path фильтрации.
- [GalleryController.java](src/main/java/ru/tattoo/maxsim/controller/client/GalleryController.java)
  и [GalleryAdminController.java](src/main/java/ru/tattoo/maxsim/controller/admin/GalleryAdminController.java):
  тонкие методы, без `prepareGalleryData`, без дублирующихся констант.
- `LkController`/`ReviewsController` переведены на `GalleryQuery`.

### Frontend

- [main.js](src/main/resources/static/js/main.js): удалён старый active-only обработчик.
- [pagination.js](src/main/resources/static/js/modules/pagination.js):
  отдельные делегированные обработчики `[data-filter-link]` и `[data-pagination-link]`;
  без `event.which`, с проверкой модификаторов; стиль активного фильтра обновляется здесь.
- Шаблоны уже отдают ссылки с `data-filter-link`, `data-filter-style`, `data-target`.
- Серверный `selectedStyle` остаётся источником active-state при полной загрузке; JS — при AJAX.

## 5. Проверки

```bash
grep -rn "gallery-controls ul li').on" src/main/resources/static/js || true   # пусто
grep -rn "event.which" src/main/resources/static/js/modules/pagination.js     # пусто
grep -rn "data-filter-link" src/main/resources/templates/gallery.html
grep -n "getGalleryPage" src/main/java/ru/tattoo/maxsim/service/interf/ImagesService.java
./mvnw -DskipTests compile
```

Тесты:
- `GalleryQueryTest` — trim/«Вся галерея».
- `GalleryPageControllerTest` — `style=  Реализм  ` → `category=Реализм`, view `gallery::galleryFilter`.
- `PaginationTemplateTest` — data-атрибуты и реальные ссылки.
- `PageResultTest`, `PageRequestUtilsTest` — контракт страницы.

Ручное: `/gallery` → стиль → сетка/активный пункт/URL; back/forward; `/admin/gallery` → фильтр;
пустой стиль; 375/768 px; без JS (обычный GET).

## 6. Риски

- Если в БД категория отличается регистром/пробелами, нужна миграция данных — trim решает только
  края.
- Админ-ссылки ведут на fragment-endpoint; без JS админка не является отдельной страницей (SPA).
- Полный Selenium/UI-прогон требует MySQL, сервера и браузера.

## 7. Definition of Done

- [x] Фильтр отправляет запрос и меняет сетку.
- [x] «Вся галерея» сбрасывает фильтр.
- [x] Один обработчик active-state.
- [x] `GalleryQuery` — единственная точка нормализации стиля.
- [x] Контроллеры без `prepareGalleryData` и дублирующихся констант.
- [x] `ImagesServiceImpl` с constructor injection и read-only транзакцией.
- [x] Тесты на нормализацию и контроллер.
