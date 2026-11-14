# PROMPT_INDEX_REFACTOR — промт для исполнителя

> Скопируй этот текст в новый чат/агента. Он самодостаточен; спека и правила лежат в репозитории.

---

Ты — ведущий Java/Thymeleaf-инженер в проекте `website-tattoos` (Spring Boot 3.4, Thymeleaf,
Bootstrap 4.4.1, jQuery-плагины). Твоя задача — **поэтапно отрефакторить главную страницу
`src/main/resources/templates/Index.html` и её стили**, не меняя визуальный результат без
согласования.

## 0. Прочитай перед началом

1. `SPEC_INDEX.md` — инвентарь, целевая структура, этапы, риски.
2. `.dsh/skills/index-refactor/SKILL.md` — обязательные правила (структура, JS, CSS, запрет inline).
3. `src/main/resources/templates/Index.html` (703 строки) и `src/main/resources/static/css/main.css`.
4. `src/main/resources/static/js/main.js`, `static/js/modules/ui.js`, `video.js`, `image.js`.

**Не начинай правки, пока не прочитал все четыре пункта.**

## 1. Контекст (подтверждённые факты)

- `Index.html` содержит 9 секций, 2 inline-скрипта Metrika, 4 inline-стиля, 15 `data-setbg`.
- `main.js` в `$(document).ready` вызывает `initStickyHeader`, `initBackgrounds`, `initMobileMenu`,
  `initOwlCarousels`, `initMagnificPopup`, `initNiceSelect`, `initTimetableFilter`,
  `initVideoCarousel`. Определения — в `modules/ui.js` и `modules/video.js`, но **на главной эти
  модули не подключены** (только `main.js`). Следствие: `ReferenceError`, фоны/карусели/меню/видео
  не работают. `sendMail` (в `modules/image.js`) тоже не определён — форма на главной падает.
- `main.css` импортирует несуществующий `pages/user.css` (есть пустой `pages/useer.css`), а
  Bootstrap подключён последним — перекрывает кастомные стили.
- В `3d9a573` удалён `style.css`, но используемые на главной `.section-title`, `.set-bg`, `.spad`,
  `.primary-btn`, пустые состояния остались без правил.
- `map-widget` и `contact-form` определены внутри `Index.html`, но используются и в `contact.html`
  (`:60`, `:85`) — при переносе обнови оба файла.

## 2. Цель и границы

**Цель:** Index — композиция из фрагментов; JS инициализируется; базовые CSS-утилиты восстановлены;
стили секций консолидированы; inline-код убран. Внешний вид — как «до» (кроме исправленных багов).

**Не делай:**
- не переписывай дизайн и не меняй тексты/структуру секций без необходимости;
- не обновляй Bootstrap/jQuery;
- не трогай контроллеры и модель, если фрагментам хватает текущих переменных;
- не переименовывай существующие классы в БЭМ (это отдельный этап, см. вопросы в спеке);
- не удаляй CSS/классы без `grep` по ВСЕМ шаблонам (`templates/**/*.html`);
- не коммить временное переключение `skipTests` в `pom.xml`.

## 3. Подготовка

```bash
git status --short                 # рабочее дерево должно быть чистым
git switch -c refactor/index-page  # или согласованное имя ветки
```

Зафиксируй «до»:
- скриншоты `/` на 1440px, 768px, 375px (или попроси владельца);
- открой консоль и сохрани текущие ошибки (ожидается `ReferenceError: initStickyHeader is not defined`);
- проверь, что приложение запускается (команда — из README/профиля проекта; локальный профиль
  `application-local.properties` использует удалённую БД — не делай destructive-операций).

## 4. Этапы (строго по одному, после каждого — стоп и отчёт)

### Этап 1. Оживить JS главной (P0)

1. Создай `src/main/resources/templates/fragments/index/scripts.html` с фрагментом `scripts`:
   jQuery → bootstrap → magnific → mixitup → nice-select → slicknav → owl → masonry →
   `modules/ui.js` → `modules/video.js` → `modules/image.js` → `main.js`.
   (Vendor-скрипты можно оставить в Index, но модули обязаны идти до `main.js`.)
2. В `Index.html` замени блок скриптов (`~:670-678`) на include нового фрагмента.
3. При необходимости добавь в `main.js` защиту `typeof fn === 'function'`, чтобы отсутствие модуля
   не обрывало всю инициализацию. Не дублируй вызовы.
4. Проверь в браузере: фоны `data-setbg` применяются, hero/видео-карусели работают, меню и
   видео-модалка открываются, консоль без `ReferenceError`.
5. Проверь `contact.html` — он тоже подключает только `main.js`. Если владелец разрешил — примени
   тот же `scripts.html`; иначе зафиксируй как отдельный тикет.
6. Коммит: `fix(index): load UI modules before main.js entrypoint`.

**Стоп.** Покажи diff, вывод консоли и что изменилось визуально.

### Этап 2. Восстановить CSS-утилиты и порядок (P0/P1)

1. Верни `.primary-btn` и `.primary-btn:after` из
   `git show 3d9a573^:src/main/resources/static/css/style.css` (строки ~177-230) в
   `components/buttons.css`, заменив hex на `var(--color-*)`/`var(--gradient-primary)`.
2. Создай `components/section-title.css` (`.section-title h2`, `.section-title p`) из старых
   строк ~133-144; цвета — переменные.
3. Создай `components/utilities.css`: `.set-bg`, `.spad`, `.spad-2`, `.visually-hidden`,
   `.metrika-pixel`. `.status-message` уже есть в `pages/map-section.css:164` — добавь туда
   `display:none` и `.status-message.is-visible` (без дубля).
4. Добавь стили пустых состояний: `.empty-hint`, `.empty-class-item`, `.empty-class-content`
   (в `pages/classes.css`, если они только там; иначе `components/empty-state.css`).
5. `main.css`: удали импорт `pages/user.css` и сам пустой `pages/useer.css`; убери дублирующие
   vendor-импорты (vendor уже в `<head>`); порядок: base → components → layouts → pages → responsive.
6. Подключи новые компоненты в `main.css`.
7. Проверь: заголовки секций, кнопки, отступы, фоны; скриншоты «после» не отличаются от «до»
   (кроме мест, где баг был очевиден).
8. Коммит: `fix(css): restore index utilities and fix import order`.

**Стоп.** Покажи diff и скриншоты.

### Этап 3. Index → композиция из фрагментов

Создай `templates/fragments/index/` и переноси **по одной секции**, после каждой — проверка страницы:

`preloader.html`, `hero.html`, `features.html`, `about.html`, `classes.html`, `price.html`,
`chooseus.html`, `video.html`, `reviews.html`, `map.html`.

Правила:
- один файл = один фрагмент с именем файла;
- сохрани все `th:each`, `th:if`, `th:with` и переменные (`carousel`, `reviewsLimit`, `contact`,
  `details`, `status`);
- `map-widget` и `contact-form` вынеси в `templates/fragments/contact/` и обнови `Index.html` **и**
  `contact.html:60,85`; `grep -rn "Index::" templates` должен стать пустым;
- `verificationMetaTags` — в `fragments/index/verification-meta.html` (или оставь в head, если
  решено не выносить);
- Index после этапа: head/SEO, preloader, header, 9 include-секций, cta, map, footer, scripts.

Проверяй после каждой секции: страница 200, секция на месте, данные/пустые состояния, консоль.
Коммит после 2-3 секций или один общий: `refactor(index): extract page sections into fragments`.

**Стоп.** Покажи `git diff --stat`, список фрагментов и проверку `/` + `/contact`.

### Этап 4. Убрать inline-код

1. `fragments/index/analytics-head.html` — VK/ads + Metrika (head, ID 112720696);
   `analytics-body.html` — Metrika (ID 98027672). Подключи include в Index.
2. Metrika noscript / honeypot → классы `.metrika-pixel`, `.visually-hidden`.
3. `.status-message` — CSS-класс вместо `style="display:none"`; в `modules/image.js` переключение
   `is-visible` (или jQuery без inline display).
4. `th:onsubmit="sendMail(...)"` → `data-form="contact"` + делегированный listener в модуле
   (не забудь `event.preventDefault()`).
5. Проверь отправку формы: успех/ошибка/валидация/honeypot; консоль без ошибок.
6. Коммит: `refactor(index): move inline analytics and handlers to fragments/modules`.

**Стоп.** Покажи diff и результат отправки формы.

### Этап 5. CSS ownership/perf (только после приёмки 1-4)

1. Раздели `pages/video.css`: публичная часть (1-380) и админская (381-591); админскую подключай
   только в `admin.html`/admin-фрагменте.
2. Вынеси admin-only CSS (`admin-common.css`, `mail.css`, `backup.css`, `lk-wrapper.css`,
   `login.css`) из `main.css` на соответствующие страницы; shared `.admin-controls` (нужен
   `fragments.html`) — в `components/`.
3. Убери `!important` в `video.css:140-150` и `map-section.css:36-37`.
4. Дубли `.instruction-note` (video/map) — в `components/`.
5. Замени hex-цвета на переменные в 9 файлах секций (по одному файлу за шаг).
6. Политику адаптива не меняй массово: новые правила — в файл секции; старые — отдельным этапом.

**Стоп.** Покажи, какие файлы стали admin-only и как проверял, что публичные страницы не потеряли
стили.

### Этап 6. Финальная верификация

```bash
grep -n "<section" src/main/resources/templates/Index.html          # пусто
grep -rn 'style=\|<style' src/main/resources/templates/Index.html \
  src/main/resources/templates/fragments/index                       # пусто
grep -rn "Index::" src/main/resources/templates                     # пусто
# битые @import
while read -r p; do test -f "src/main/resources/static/css/${p}" || echo "НЕТ: $p"; done \
  < <(grep -oP "@import '\K[^']+" src/main/resources/static/css/main.css)
./mvnw -DskipTests compile
git diff --check
```

Плюс:
- `/`, `/contact`, `/admin`, `/lk`, `/reviews` — 200 и без визуальных регрессий;
- консоль без `ReferenceError`;
- форма на главной отправляется;
- нет новых `!important` и inline-стилей;
- `pom.xml` не содержит незакоммиченного переключения `skipTests`.

## 5. Формат отчёта после каждого этапа

1. Что сделано (файлы).
2. Вывод проверок (grep/compile/консоль/скриншоты).
3. Что осталось / риски.
4. Коммит (хеш и сообщение).
5. Вопросы, если есть.

Не переходи к следующему этапу без явного «продолжай» от владельца.
