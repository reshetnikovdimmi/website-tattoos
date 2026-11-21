# PROMPT_CAROUSEL_REFACTOR — промт для исполнителя

> Скопируй в новый чат/агента. Сначала прочитай `SPEC_CAROUSEL.md` и
> `.dsh/skills/carousel-refactor/SKILL.md`.

---

Ты — ведущий Java/Thymeleaf/JS-инженер проекта `website-tattoos`. Нужно объединить публичную
карусель Index и админское «Превью карусели» в один компонент: один Owl-движок, один контроллер,
один CSS и одинаковое управление.

## 0. Контекст

- Публичная: `templates/fragments/index/hero.html` — `.hero-section` / `.hero-items.owl-carousel`,
  инициализируется в `static/js/modules/ui.js` (`initOwlCarousels`, строки 89-103).
- Админка: `templates/fragment-admin/home.html:130-221` — `#adminCarousel.admin-carousel.carousel.slide`
  (Bootstrap-разметка), управляется `static/js/modules/carousel.js` (62 строки, хардкод `#adminCarousel`).
- CSS: `pages/hero.css` (217 строк) и `pages/admin-common.css:360-520`; адаптив в
  `responsetive/tablet.css` (717-750, 995) и `responsetive/mobile.css` (319-330, 454).
- Данные: `carousel[0].banners` (Index) и `home[0].banners` (админка); backend не менять.

**Осторожно:** в рабочем дереве может быть незакоммиченное изменение `fragment-admin/home.html`.
Проверь `git status`; не перезаписывай чужие правки без согласования.

## 1. Цель и границы

**Цель:** обе карусели используют Owl, `data-carousel` и `CarouselModule`; общий CSS
`components/carousel.css`; одинаковые стрелки/точки; AJAX-обновление превью работает.

**Не делать:**
- не менять контроллеры/модели/БД;
- не обновлять Bootstrap/Owl;
- не переписывать дизайн hero-текста и админских карточек;
- не удалять CSS без `grep` по шаблонам;
- не оставлять Bootstrap-carousel API/разметку для этих двух каруселей.

## 2. Подготовка

```bash
git status --short
git switch -c refactor/carousel   # или согласованное имя
./mvnw -DskipTests compile
node --check src/main/resources/static/js/modules/carousel.js
```

Скриншоты «до»: `/` (hero) и `admin → Home → Превью карусели` на 1440/768/375 px.

## 3. Этапы (после каждого — стоп и отчёт)

### Этап 1. Единый JS-контроллер + публичная карусель

1. Перепиши `static/js/modules/carousel.js`:
   - `initAll(scope)`, `refresh(scope)`, `next(root)`, `prev(root)`, `goTo(root, index)`;
   - опции из `data-carousel-*` (loop/nav/dots/autoplay/speed);
   - никакого `#adminCarousel`, `data('bs.carousel')`;
   - `refresh` = destroy Owl + повторная инициализация;
   - автоинициализация `[data-carousel]` на `ready`.
2. В `templates/fragments/index/hero.html` переведи корень на
   `carousel carousel--hero owl-carousel` + `data-carousel`; слайды — `.carousel__slide`
   (можно сохранить `.single-hero-item` вторым классом). Убери `style`/лишние обёртки, если есть.
3. В `ui.js` убери инициализацию `.hero-items` (оставь `testimonial-slider` до отдельного этапа).
4. В `fragments/scripts.html` подключи `modules/carousel.js` после модулей и до `main.js`.
5. Проверь Index: стрелки, точки, свайп, анимация текста, пустое состояние, консоль.

Коммит: `refactor(carousel): add shared owl controller and migrate hero`.

**Стоп.** Diff + скриншоты + консоль.

### Этап 2. Админское превью на том же контроллере

1. В `fragment-admin/home.html:130-221` замени Bootstrap-карусель на контракт:
   ```html
   <div id="adminCarousel" class="carousel carousel--admin owl-carousel"
        data-carousel data-carousel-autoplay="false">
     <div class="carousel__slide">…</div>
   </div>
   ```
   Убери `carousel slide`, `data-ride`, `.carousel-inner`, `.carousel-item`, `.carousel-caption`,
   `.carousel-indicators`, `.carousel-controls`.
2. Кнопки навигации/точек — Owl (`.owl-nav`/`.owl-dots`). Удали
   `onclick="goToSlide(...)"`, `onclick="carouselPrev()"`, `onclick="carouselNext()"`.
3. Удаление слайда: оставь `data-fragment-name`, `data-container-selector`, `data-deletion-url`
   и добавь `data-carousel-delete`; обработчик — делегированный (в admin/ carousel module),
   без inline `onclick`.
4. В `AdminModule.reinitAfterFragmentUpdate` добавь `CarouselModule.refresh('.tab-content')`, если в
   контейнере есть `[data-carousel]` (для home/hero).
5. Проверь админку: превью, стрелки, точки, удаление, обновление фрагмента через AJAX.

Коммит: `refactor(carousel): migrate admin preview to shared owl component`.

**Стоп.** Diff + проверка админки.

### Этап 3. CSS-компонент

1. Создай `static/css/components/carousel.css`:
   - `.carousel`, `.carousel__slide`, `.carousel__caption`, `.carousel__delete`;
   - `.carousel .owl-nav button`, `.carousel .owl-dots button` — единые стрелки/точки;
   - модификаторы `--hero` и `--admin`;
   - адаптив 992/768/480.
2. Перенеси общие правила из `pages/hero.css` (nav/dots) и `admin-common.css:360-520`
   (оболочка, контролы, индикаторы, delete); удали дубли.
3. В `pages/hero.css` оставь только hero-текст/высоту/анимации; в `admin-common.css` — только
   админскую специфику превью.
4. Подключи компонент в `main.css`; **cache-bust** `v=5 → v=6`:
   - `?v=6` у `main.css` во всех 10 шаблонах;
   - `?v=6` у `@import` изменённых файлов (`components/carousel.css`, `pages/hero.css`,
     `pages/admin-common.css`).
5. Проверь desktop/tablet/mobile; сравни скриншоты «до/после».

Коммит: `refactor(carousel): share carousel styles and bust css cache`.

**Стоп.** Diff + скриншоты + cache-bust проверка.

### Этап 4. Очистка (если согласовано)

1. Общий `templates/fragments/carousel/slide.html` с `slide(banner, mode)` — опционально.
2. Удали легаси `window.carouselPrev/Next/goToSlide`, если больше не используются.
3. Удали мёртвые `.admin-carousel`/`.custom-carousel-btn`/`.carousel-indicators` после `grep`.
4. `grep`-проверки; обнови `SPEC_CAROUSEL.md` (статус).

## 4. Проверки

```bash
grep -rn "owlCarousel\|data-carousel" src/main/resources/static/js/modules
grep -rn "hero-items" src/main/resources/static/js/modules/ui.js          # не инициализирует
grep -rn "carousel slide\|data-ride\|carousel-inner\|carousel-item" \
  src/main/resources/templates/fragments/index/hero.html \
  src/main/resources/templates/fragment-admin/home.html                   # пусто
node --check src/main/resources/static/js/modules/carousel.js
./mvnw -DskipTests compile
git diff --check
```

Ручное: `/` (стрелки/точки/свайп/анимация), `admin → Home → превью` (стрелки/точки/удаление,
AJAX-обновление), 375/768 px, консоль без ошибок.

## 5. Формат отчёта

1. Что изменено (файлы).
2. Вывод проверок (grep/compile/консоль/скриншоты).
3. Риски/что осталось.
4. Коммит (хеш, сообщение).
5. Вопросы.

Не переходи к следующему этапу без явного «продолжай».
