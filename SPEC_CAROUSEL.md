# SPEC_CAROUSEL.md — единая карусель для Index и админского превью

> **Статус: только анализ.** HTML/CSS/JS не изменялись. План — после подтверждения.
> Правила: `.dsh/skills/carousel-refactor/SKILL.md`. Промт: `.dsh/skills/carousel-refactor/PROMPT.md`.

## 1. Что есть сейчас

| | Публичная (Index) | Админка («Превью карусели») |
|---|---|---|
| Разметка | `templates/fragments/index/hero.html:1-55` | `templates/fragment-admin/home.html:130-221` |
| Корень | `.hero-section` → `.hero-items.owl-carousel` | `#adminCarousel.admin-carousel.carousel.slide` + `data-ride="carousel"` |
| Слайд | `.single-hero-item.set-bg` + `data-setbg` | `.carousel-item` (Bootstrap) + `<img>` |
| Контент | `h2` / `h1` / `.primary-btn` | `.carousel-caption h6/h5` + `.carousel-delete-btn` |
| Навигация | Owl генерирует `.owl-nav` / `.owl-dots` | свои `.carousel-controls` + `.custom-carousel-btn`; индикаторы `.carousel-indicators button` |
| JS | `modules/ui.js:89-103` `initOwlCarousels()` → `.hero-items` | `modules/carousel.js` (62 строки) → `#adminCarousel`, Bootstrap/fallback |
| CSS | `pages/hero.css` (217 строк) + `responsetive/*` | `pages/admin-common.css:360-520` + `responsetive/*` |
| Подключение | `fragments/scripts.html` (owl + ui.js + main.js) | `admin.html:24,148,160` (owl + carousel.js) |
| Данные | `carousel[0].banners` | `home[0].banners` |

Обе карусели показывают один и тот же тип сущности — баннеры (`imageName`, `title`, `subtitle`), но
реализованы двумя разными способами.

## 2. Различия и проблемы

1. **Два движка:** публичная — Owl Carousel, админская — Bootstrap carousel + fallback на ручное
   переключение `.active`.
2. **Два контроллера:** `initOwlCarousels()` в `ui.js` и `carousel.js`; `carousel.js` хардкодит
   `#adminCarousel` и проверяет `data('bs.carousel')`/`data('carousel')`.
3. **Разная разметка:** `owl-carousel/owl-item` против `carousel-inner/carousel-item`; нельзя просто
   подключить Owl к текущей админской разметке — Bootstrap `.carousel-item { display:none }` скроет
   слайды.
4. **Разный стиль управления:** hero — крупные стрелки `arrow_carrot-*` по краям и точки внизу;
   админка — круглые `custom-carousel-btn` и индикаторы-кнопки. Визуально это два разных компонента.
5. **Inline-обработчики:** `th:onclick="goToSlide(...)"`, `onclick="carouselPrev()"`,
   `onclick="carouselNext()"`, `onclick="deleteImage(this)"`.
6. **AJAX-обновление админки:** `AdminModule.updateAdminFragment` заменяет `.tab-content`, после чего
   карусель не переинициализируется (в `reinitAfterFragmentUpdate` нет условия для home/hero).
7. **Дубли CSS:** `.carousel-controls`, `.custom-carousel-btn`, `.carousel-indicators` — только
   админка; `.owl-nav`/`.owl-dots` — только hero. Общей оболочки нет.
8. **Bootstrap 4 + Owl одновременно:** обе библиотеки подключены, но карусели используют разные API.

## 3. Целевая архитектура

### 3.1. Один движок — Owl Carousel

Owl уже подключён на обеих страницах (`owl.carousel.min.css/js`), поэтому оставляем его. Bootstrap-
карусель из админки убираем полностью; Bootstrap CSS/JS остаются для других компонентов.

### 3.2. Один контроллер — `modules/carousel.js`

```js
CarouselModule.initAll(scope)      // [data-carousel] в scope (default: document)
CarouselModule.refresh(scope)      // destroy + initAll; для AJAX-фрагментов
CarouselModule.next(root)
CarouselModule.prev(root)
CarouselModule.goTo(root, index)
```

Опции читаются из `data-*`:

| Атрибут | По умолчанию |
|---|---|
| `data-carousel` | маркер инициализации |
| `data-carousel-loop` | `true` |
| `data-carousel-nav` | `true` |
| `data-carousel-dots` | `true` |
| `data-carousel-autoplay` | `false` |
| `data-carousel-speed` | `1200` |

Требования:

- не хардкодить `#adminCarousel`;
- не использовать Bootstrap API;
- `refresh` делает `destroy.owl.carousel` + очистку `.owl-*` перед повторной инициализацией;
- инициализация на `$(document).ready` + возможность повторного вызова;
- легаси `window.carouselPrev/Next/goToSlide` — только на период миграции, затем удалить.

### 3.3. Контракт разметки

```html
<!-- публичная -->
<section class="hero-section">
  <div class="carousel carousel--hero owl-carousel" data-carousel
       data-carousel-loop="true" data-carousel-nav="true" data-carousel-dots="true">
    <div class="carousel__slide single-hero-item">…</div>
  </div>
</section>

<!-- админка -->
<div id="adminCarousel" class="carousel carousel--admin owl-carousel" data-carousel
     data-carousel-autoplay="false">
  <div class="carousel__slide">
    <img …>
    <div class="carousel__caption">…</div>
    <button class="carousel__delete" data-carousel-delete data-id="…">…</button>
  </div>
</div>
```

Правила:

- никаких `carousel slide`, `data-ride`, `carousel-inner`, `carousel-item`, `carousel-caption`,
  `carousel-indicators`, `carousel-controls`;
- навигация/точки — Owl (`.owl-nav`, `.owl-dots`), стилизуются общим компонентом;
- `onclick` для навигации/удаления убрать; вместо них — делегированные обработчики по
  `data-carousel-*`;
- уникальный `id` не обязателен; контроллер работает через `data-carousel`.

### 3.4. Один CSS-компонент

Новый `static/css/components/carousel.css`:

- `.carousel`, `.carousel__slide`, `.carousel__caption`, `.carousel__delete`;
- `.carousel .owl-nav button`, `.carousel .owl-dots button` — единые стрелки/точки;
- модификаторы `.carousel--hero` (full-height, текст/анимации остаются в `hero.css`) и
  `.carousel--admin` (высота 320 px, hover-zoom, caption, delete);
- адаптив: точки/стрелки для 992/768/480.

Перераспределение:

| Откуда | Куда |
|---|---|
| `hero.css`: `.hero-items .owl-nav`, `.hero-items .owl-dots` (строки ~150-217) | `components/carousel.css` (`.carousel .owl-nav/.owl-dots`) |
| `admin-common.css:360-520`: `.admin-carousel`, `.carousel-controls`, `.custom-carousel-btn`, `.carousel-indicators`, `.carousel-delete-btn` | `components/carousel.css` + `.carousel--admin` |
| `hero.css`: `.single-hero-item`, `.hero-text`, `.primary-btn` | остаётся в `hero.css` (только hero-специфика) |
| `responsetive/tablet.css:717-750,995`, `mobile.css:319-330,454` | общий компонент/hero по принадлежности |

`main.css` импортирует `components/carousel.css`; после изменений — **cache-busting** `v=4 → v=5`
(см. §7).

### 3.5. Общие фрагменты (опционально, этап 4)

Вынести слайд в `templates/fragments/carousel/slide.html`:

```html
<div th:fragment="slide(banner, mode)" class="carousel__slide">
  <!-- mode='hero': data-setbg + .hero-text -->
  <!-- mode='admin': img + .carousel__caption + .carousel__delete -->
</div>
```

Тогда `hero.html` и `fragment-admin/home.html` используют один слайд-фрагмент с разным `mode`.
Если это усложняет шаблоны — достаточно общего CSS/JS и одинаковых классов (минимум).

## 4. План этапов

### Этап 0. Базовая линия

- Скриншоты `/` (hero) и `admin → Home → Превью карусели` до изменений.
- `git status` чистый; ветка `refactor/carousel`.
- Зафиксировать текущие ошибки консоли (если есть).

### Этап 1. Единый JS-контроллер + публичная карусель

1. Переписать `modules/carousel.js` под API из §3.2, с `data-carousel` и `refresh`.
2. Убрать инициализацию `.hero-items` из `ui.js` (оставить `testimonial-slider` до отдельного этапа).
3. Подключить `carousel.js` в `fragments/scripts.html` (после модулей, до `main.js`).
4. Разметку hero перевести на `carousel carousel--hero owl-carousel` + `data-carousel`; слайды —
   `.carousel__slide` (можно оставить `.single-hero-item` как дополнительный класс).
5. Проверить: Index — стрелки, точки, свайп, анимация текста, пустое состояние.

### Этап 2. Админское превью на том же контроллере

1. Заменить Bootstrap-разметку `#adminCarousel` на Owl-контракт из §3.3.
2. Убрать `data-ride`, `.carousel-inner/item/...`, `.carousel-controls/indicators`.
3. Кнопки навигации/точек — Owl; удаление — `data-carousel-delete` + делегированный обработчик
   (вызов существующего `deleteImage` через `fetch`/admin module).
4. В `AdminModule.reinitAfterFragmentUpdate` добавить `CarouselModule.refresh('.tab-content')` для
   home/hero.
5. Проверить: админка → Home → превью (стрелки, точки, удаление), обновление фрагмента (AJAX),
   пустое состояние.

### Этап 3. CSS-компонент

1. Создать `components/carousel.css`; перенести общие стили из `hero.css` и `admin-common.css`.
2. Удалить дубли и Bootstrap-карусельные селекторы.
3. Вынести адаптив из `responsetive/*` в компонент/модификаторы.
4. Подключить в `main.css`; bump `v=5` и `?v=5` у изменённых импортов; обновить `main.css?v=4` в
   10 шаблонах.
5. Проверить визуально desktop/tablet/mobile; сравнить с скриншотами.

### Этап 4. Очистка (опционально)

1. Общий `slide.html` (если решено).
2. Удалить легаси `window.carouselPrev/Next/goToSlide`, `#adminCarousel`-специфику.
3. Удалить старые `.admin-carousel`/`.custom-carousel-btn`/`.carousel-indicators` из
   `admin-common.css`, если не используются.
4. `grep`-проверки; обновить спеку.

## 5. Критерии готовности

```bash
# 1. один контроллер
grep -rn "owlCarousel\|data-carousel" src/main/resources/static/js/modules
grep -rn "hero-items" src/main/resources/static/js/modules/ui.js   # не инициализирует

# 2. нет Bootstrap-карусели в hero/admin preview
grep -rn "carousel slide\|data-ride\|carousel-inner\|carousel-item\|carousel-caption" \
  src/main/resources/templates/fragments/index/hero.html \
  src/main/resources/templates/fragment-admin/home.html

# 3. общий CSS
test -f src/main/resources/static/css/components/carousel.css
grep -rn "carousel__\|owl-nav\|owl-dots" src/main/resources/static/css/components/carousel.css

# 4. cache-bust
grep -rn "main.css?v=4" src/main/resources/templates --include=*.html | wc -l   # 10
grep -n "carousel.css?v=4" src/main/resources/static/css/main.css
```

Ручная проверка: Index (стрелки/точки/свайп/анимация), админка (превью, удаление, AJAX-обновление),
375/768 px, консоль без ошибок.

## 6. Риски

1. **Owl + Bootstrap CSS:** после удаления Bootstrap-разметки убедиться, что `.carousel-item` больше
   нигде не используется (иначе стили Bootstrap скроют слайды).
2. **Двойная инициализация:** если `ui.js` продолжит инициализировать `.hero-items`, появятся две
   навигации.
3. **AJAX-фрагменты:** без `CarouselModule.refresh` превью после обновления не работает.
4. **Удаление слайда:** обработчик `deleteImage` завязан на `data-*`; при переносе кнопки сохранить
   `data-fragment-name`, `data-container-selector`, `data-deletion-url`.
5. **Кэш CSS:** без `v=5` Cloudflare отдаст старый `main.css`/`hero.css`/`admin-common.css`.
6. **Визуальная регрессия:** hero-текст/анимации и caption в админке могут отличаться; сверять
   скриншоты.
7. **Тесты/UI:** проверить, что селекторы Selenium не завязаны на `.carousel-item`/`#adminCarousel`
   (поиск по `src/test/java`).

## 7. Cache-busting

После любого изменения CSS:

1. версия `v=4` → `v=5`;
2. `?v=5` у `main.css` во всех 10 шаблонах (`grep -rln "static/css/main.css" templates`);
3. `?v=5` у `@import` изменённых файлов в `main.css`:
   ```css
   @import 'components/carousel.css?v=5';
   @import 'pages/hero.css?v=5';
   @import 'pages/admin-common.css?v=5';
   ```
4. проверить `curl /static/css/main.css?v=5` и страницу на проде.

## 8. Открытые вопросы

1. Оставляем Owl или переходим на ванильный контроллер? Рекомендация — Owl (уже загружен).
2. Выносим ли слайд в общий `slide.html` или ограничиваемся общими классами/CSS?
3. Нужен ли autoplay в админском превью (сейчас `data-ride` подразумевал его)?
4. Оставляем ли `testimonial-slider` в `ui.js` или переносим все карусели в `carousel.js`?
5. Иконки стрелок: `arrow_carrot-*` (hero) или `fa-chevron-*` (admin)? Выбрать один стиль.
