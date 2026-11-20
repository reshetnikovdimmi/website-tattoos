---
name: header-refactor
description: Use when changing the global site header or mobile menu — extracting the header fragment, deduplicating desktop/mobile navigation, fixing module loading, cleaning header CSS, or making the burger menu work on every page.
---

# Header / Mobile Menu Refactor (правила проекта)

## Overview

Хедер один для всех страниц: один Thymeleaf-фрагмент, один источник навигации, один CSS-владелец и
один JS-инициализатор. Если меню работает на `/` и `/admin`, оно обязано работать на всех страницах,
которые подключают тот же `fragments::header`.

## Hard rules

### 1. Один фрагмент и одно место

- Целевое место: `src/main/resources/templates/fragments/header/header.html` с фрагментом `header`.
- `fragments.html` не должен содержать разметку хедера/меню после переноса — только include.
- Навигация: **один список ссылок**. Десктопное и мобильное меню — два вызова одного фрагмента
  `nav` с разным CSS-классом/модификатором, а не две скопированные `<ul>`.
- Соцсети/авторизация — общие вложенные фрагменты (`socials`, `auth-actions`), без дублей.
- Логотип — один фрагмент `logo`; `id` логотипа уникален на странице.

### 2. Никакого inline-кода

- Запрещены `style="..."` и `th:style` в хедере и мобильном меню.
- Logout — только `POST` с CSRF (`th:action="@{/logout}" method="post"`), без inline-стилей; вид
  задаётся классами.
- Никаких inline `<script>` и `onclick` в разметке хедера.

### 3. JS-контракт

- `initMobileMenu()` живёт в `static/js/modules/ui.js`.
- `modules/ui.js` обязан подключаться на **каждой** странице, где есть `fragments::header`, **до**
  `main.js`. Предпочтительно — через общий фрагмент скриптов, а не копипастой по страницам.
- `main.js` вызывает `initMobileMenu()` только при `typeof … === 'function'`; если модуль не
  подключён — меню молча не работает, поэтому проверка загрузки обязательна.
- `initMobileMenu` должен:
  - открывать/закрывать по `.mobile-menu-toggle-icon` (`preventDefault`);
  - закрывать по `.mobile-menu-close`, `.mobile-menu-overlay` и клику по ссылке `.mobile-nav a`;
  - закрывать по `Escape`;
  - переключать `body.menu-open` и `aria-expanded`;
  - быть идемпотентным (`.off('.mobileMenu').on(...)`) — повторный вызов не плодит обработчики.
- Мёртвый `slicknav`-код (`.mobile-menu`, `#mobile-menu-wrap`) не возвращать: в проекте используется
  кастомный сайдбар.

### 4. CSS-контракт

- Единственный владелец стилей хедера и мобильного меню — `static/css/layouts/header.css`.
- Дубли `.mobile-menu-*`/`.mobile-nav*` из `static/css/layouts/sidebar.css` удаляются; `!important`
  не добавляются (в `sidebar.css` их 24 — это техдолг).
- Новые стили — на переменных из `base/variables.css`, без магических hex и `!important`.
- `body.menu-open { overflow: hidden; }` остаётся в `header.css` (без дубля в `sidebar.css`).
- Адаптив: правила бургер-меню — в `header.css`; глобальные `responsetive/*` не дублируют те же
  селекторы.

### 5. Доступность

- Бургер: `aria-label`, `aria-expanded`, `aria-controls="mobile-menu-sidebar"`.
- Сайдбар: `id="mobile-menu-sidebar"`; кнопка закрытия — `type="button"` + `aria-label`.
- `Escape` закрывает меню; фокус после закрытия возвращается на бургер (по возможности).
- Ссылки навигации остаются обычными `<a>`; активный пункт — `servletPath`, а не JS.

### 6. Проверка перед коммитом

```bash
# ui.js есть на всех страницах с header и идёт до main.js
grep -rn "fragments::header" src/main/resources/templates --include=*.html
grep -rn "modules/ui.js\|fragments/index/scripts" src/main/resources/templates --include=*.html

# нет inline-стилей в хедере
grep -n 'style="' src/main/resources/templates/fragments.html \
  src/main/resources/templates/fragments/header/*.html
```

Плюс ручная проверка на ширине 375/768 px: открыть/закрыть меню, клик по фону, `Esc`, клик по
ссылке; консоль без ошибок; на всех страницах из списка.

## Common mistakes

- Добавить `ui.js` только на главную/админку — остальные страницы остаются без меню.
- Подключить `ui.js` **после** `main.js` — `$(document).ready` уже прошёл, меню не инициализируется.
- Оставить две копии `<ul>` для десктопа и мобилки — пункты расходятся.
- Починить меню, но оставить `body.menu-open` только в `sidebar.css` с `!important`.
- Вернуть `slicknav`-вызов на несуществующий `.mobile-menu`.
- Удалить `aria-expanded`/`aria-controls`, потому что «и так работает».
