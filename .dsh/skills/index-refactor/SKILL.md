---
name: index-refactor
description: Use when refactoring the main page (Index.html) or its styles — extracting sections into Thymeleaf fragments, fixing JS initialization, restoring missing utilities, cleaning page CSS, and removing inline styles.
---

# Index Page Refactor (правила проекта)

## Overview

`Index.html` — это **композиция**, а не свалка разметки: SEO-head, includes секций и scripts. Каждая
секция — отдельный Thymeleaf-фрагмент. Стили секции живут в её CSS-файле, общие вещи — в `components/`.

Цель — чтобы главную можно было менять по секциям, не ломая JS, другие страницы и чужие стили.
**Визуальный результат не меняется** без отдельного решения владельца.

## Hard rules

### 1. Структура шаблонов

- `templates/Index.html` содержит только: `<head>` (SEO/мета/CSS), preloader, includes секций,
  `fragments::cta-section`, `fragments::footer`, scripts и analytics-body.
- Секции: `templates/fragments/index/` — один файл = одна секция = один `th:fragment`,
  имя фрагмента = имя файла (`hero`, `features`, `about`, `classes`, `price`, `chooseus`, `video`,
  `reviews`, `map`).
- Общие фрагменты, используемые несколькими страницами (`map-widget`, `contact-form`), — в
  `templates/fragments/contact/`, а не в `fragments/index/`.
- Секция не должна требовать новых переменных модели. Если требует — это меняет контракт контроллера,
  значит нужно отдельное решение и правка `HomeController`.
- Внутри секции сохраняются существующие `th:each`/`th:if`/`th:with`; итерацию не переносить в Java.

### 2. Никакого inline-кода

- Запрещены `<style>`, `style="..."`, `th:style`, inline `<script>` в Index и секциях.
- Аналитика и сторонние сниппеты — в `templates/fragments/index/analytics-head.html` и
  `analytics-body.html` (или в `fragments/analytics/`), а не в разметке секций.
- JS-обработчики — в `static/js/modules/`, разметка использует `data-*`-хуки. Никаких `onclick`,
  `onsubmit`, `onchange` в новых/перенесённых секциях.
- Исключение проекта: `data-setbg="..."` — это data-атрибут, а не inline-style; его использует
  `initBackgrounds()` из `modules/ui.js`. Не заменять его на `style="background-image:..."`.

### 3. JS-контракт

- `static/js/main.js` — единственная точка входа для публичных страниц.
- Модули (`modules/ui.js`, `modules/video.js`, `modules/image.js`) подключаются **до** `main.js`:
  `main.js` в `$(document).ready` вызывает их функции.
- Нельзя вызывать функцию, модуль которой не подключён на странице. Перед добавлением вызова —
  проверить `grep` по шаблонам.
- Каждая init-функция должна быть идемпотентной: повторный вызов не плодит обработчики/слайдеры.
- `sendMail()` живёт в `modules/image.js`; без его подключения форма на главной не работает.
- Порядок скриптов: jQuery → плагины → модули → `main.js`.

### 4. CSS-контракт

- Вендорные стили (`bootstrap`, `font-awesome`, `elegant-icons`, `nice-select`, `owl`, `magnific`,
  `slicknav`) подключаются `<link>` в шаблоне. `main.css` содержит **только** стили проекта.
- Порядок в `main.css`: `base` → `components` → `layouts` → `pages` → `responsetive`.
- Общие утилиты/компоненты — `components/` (например, `.primary-btn`, `.section-title`, `.set-bg`,
  `.spad`, `.visually-hidden`). Стили конкретной секции — её `pages/<section>.css`.
- Новые стили: CSS-переменные из `base/variables.css`, без `!important`, без магических hex-цветов,
  без `style`-атрибутов.
- Не удалять CSS-файл/селектор, пока `grep` по всем `templates/**/*.html` не доказал, что класс не
  используется. Урок `style.css`, удалённого в `3d9a573`.
- Новые классы — по БЭМ (`index-hero`, `index-hero__title`). Существующие плоские классы не
  переименовываются в рамках структурного этапа — только отдельным согласованным этапом.

### 5. Верификация

Перед коммитом каждого этапа:

```bash
# нет inline-стилей/скриптов в Index и секциях
grep -rn 'style=\|<style' src/main/resources/templates/Index.html \
  src/main/resources/templates/fragments/index

# все вызываемые JS-функции существуют и подключены
grep -n 'init[A-Z]\|sendMail' src/main/resources/static/js/main.js
grep -rn 'modules/ui.js\|modules/video.js\|modules/image.js' src/main/resources/templates/Index.html

# нет мёртвых импортов CSS
grep -n "@import" src/main/resources/static/css/main.css
```

Плюс: `./mvnw -DskipTests compile`, проверка страниц `/`, `/contact`, `/admin` (регресс из-за общих
фрагментов), консоль браузера без `ReferenceError`, визуальное сравнение скриншотов до/после.

### 6. Stop-gate

1. Спека (`SPEC_INDEX.md`) согласована.
2. Один этап = один коммит; после этапа — отчёт и стоп.
3. Удаление старого кода — только после зелёной проверки нового.

## Quick reference

| Что | Куда |
|---|---|
| Секции главной | `templates/fragments/index/{hero,features,about,classes,price,chooseus,video,reviews,map}.html` |
| Общие map/contact-фрагменты | `templates/fragments/contact/{map-widget,contact-form}.html` |
| Аналитика | `templates/fragments/index/analytics-head.html`, `analytics-body.html` |
| Скрипты | `templates/fragments/index/scripts.html` |
| Общие CSS-компоненты | `static/css/components/` |
| CSS секций | `static/css/pages/<section>.css` |
| JS-модули | `static/js/modules/` |

## Common mistakes

- Перенести секцию в фрагмент и забыть `th:each`-переменную (`current`, `reviewsLimit`, `aboutList`).
- Подключить `main.js`, но не подключить `ui.js`/`video.js`/`image.js` — главная «мертвая».
- Удалить «неиспользуемый» `style.css`/селектор без `grep` — так уже потеряли `.section-title`.
- Добавить `!important` вместо повышения специфичности.
- Оставить `pages/user.css` в импортах, хотя файла нет (а `useer.css` пустой).
- Переименовать классы в БЭМ и потерять визуальное соответствие.
