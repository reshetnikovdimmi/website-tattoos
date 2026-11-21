---
name: carousel-refactor
description: Use when unifying the public hero carousel (Index) with the admin carousel preview — shared markup/CSS, one Owl-based controller, reinitialization after AJAX, and consistent navigation.
---

# Carousel Refactor (правила проекта)

## Overview

В проекте две карусели одного и того же набора баннеров:

- публичная — `.hero-section` / `.hero-items` на Index (Owl Carousel, `initOwlCarousels` в `ui.js`);
- админская — «Превью карусели» с `#adminCarousel` (Bootstrap-разметка + `modules/carousel.js`).

Их нужно привести к **одному движку, одному контроллеру, одному CSS-компоненту и одному контракту
разметки**. Данные не меняются: публично `carousel[0].banners`, в админке `home[0].banners`.

## Hard rules

### 1. Один движок — Owl Carousel

- Обе карусели используют **Owl Carousel** (он уже подключён и на публичных страницах, и в админке).
- Нельзя использовать Bootstrap-carousel API/разметку (`carousel slide`, `data-ride`, `.carousel-inner`,
  `.carousel-item`, `$carousel.carousel(...)`) для новых/переносимых каруселей.
- Нельзя оставлять два независимых инициализатора (`.hero-items` в `ui.js` + логика в `carousel.js`).

### 2. Один контроллер

- `static/js/modules/carousel.js` — единственное место управления каруселями.
- Публичный API:
  ```js
  CarouselModule.initAll(scope)      // инициализирует [data-carousel] в scope
  CarouselModule.refresh(scope)      // destroy + initAll (после AJAX)
  CarouselModule.next(root)
  CarouselModule.prev(root)
  CarouselModule.goTo(root, index)
  ```
- Контроллер не хардкодит `#adminCarousel` и не проверяет `data('bs.carousel')`.
- Легаси-алиасы `window.carouselPrev/Next/goToSlide` можно оставить только на время миграции; после
  перевода разметки на `data-carousel-*` удалить.
- Карусель инициализируется по `data-carousel`, а не по конкретному классу страницы.

### 3. Контракт разметки

Корень:

```html
<div class="carousel carousel--hero owl-carousel" data-carousel
     data-carousel-loop="true" data-carousel-nav="true" data-carousel-dots="true">
```

- модификаторы: `carousel--hero` (публичная), `carousel--admin` (превью в админке);
- слайд: `.carousel__slide` (герой может дополнительно иметь `.single-hero-item`);
- подпись: `.carousel__caption`; админское удаление: `.carousel__delete`;
- навигация/точки — генерируются Owl (`.owl-nav`, `.owl-dots`) и стилизуются общим CSS;
- **никаких** `onclick="carouselNext()"`, `onclick="goToSlide()"`, `data-ride`, Bootstrap-классов
  карусели;
- уникальный `id` только если он реально нужен; контроллер работает от `data-carousel`, а не от id.

### 4. Один CSS-компонент

- Общая оболочка: `static/css/components/carousel.css` — позиционирование, слайд, подпись, кнопки
  `.owl-nav`, точки `.owl-dots`, модификаторы `--hero`/`--admin`, адаптив.
- `pages/hero.css` — только текстовые блоки/анимации/высота hero, без дублей навигации и точек.
- `pages/admin-common.css` — только админская специфика (delete, caption, stats); дубли
  `.carousel-inner`/`.carousel-item`/`.carousel-controls`/`.carousel-indicators` удаляются.
- Не дублировать одни и те же `.owl-nav`/`.owl-dots` в `hero.css`, `admin-common.css` и
  `responsetive/*`.
- После **любого** изменения CSS — cache-busting: текущая версия `v=4`, следующая `v=5`; обновить
  `main.css?v=N` во всех шаблонах и `?v=N` у `@import` изменённых файлов (правило из
  `header-refactor`).

### 5. Переинициализация после AJAX

- Админка заменяет `.tab-content` (`AdminModule.updateAdminFragment`) — после этого карусель нужно
  пересоздать:
  ```js
  CarouselModule.refresh('.tab-content');
  ```
- В `reinitAfterFragmentUpdate` добавить условие для `home`/`hero`, иначе превью останется пустым
  или с неработающими стрелками.
- `refresh` обязан сначала `destroy` старый Owl, иначе дублируются обработчики/стрелки.

### 6. Верификация

```bash
# один инициализатор
grep -rn "owlCarousel\|data-carousel" src/main/resources/static/js/modules
grep -rn "hero-items" src/main/resources/static/js/modules/ui.js   # не должно инициализировать

# нет Bootstrap-карусели
grep -rn "carousel slide\|data-ride\|carousel-inner\|carousel-item" \
  src/main/resources/templates/fragments/index/hero.html \
  src/main/resources/templates/fragment-admin/home.html

# общий CSS
grep -rn "carousel__\|owl-nav\|owl-dots" src/main/resources/static/css/components/carousel.css
```

Ручная проверка: Index (стрелки, точки, свайп, анимация текста), админка → Home → превью
(стрелки, точки, удаление, после AJAX-обновления фрагмента), мобильная ширина 375/768 px.

## Common mistakes

- Оставить Bootstrap `carousel-item` и подключить Owl — слайды скрыты/ломаются.
- Инициализировать `.hero-items` в `ui.js` и одновременно в `carousel.js` — двойные стрелки/точки.
- Хардкодить `#adminCarousel` — вторая карусель не заведётся.
- Забыть `CarouselModule.refresh` после `updateAdminFragment`.
- Перенести стили в `components/carousel.css`, но оставить старые `.custom-carousel-btn` в
  `admin-common.css` — визуальный разнобой.
- Забыть поднять `v=5` и `?v=5` у `@import` — Cloudflare отдаст старый CSS.
