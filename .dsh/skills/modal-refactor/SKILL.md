---
name: modal-refactor
description: Use when adding, moving, renaming, or restyling modal windows in this project's Thymeleaf templates, or when removing inline styles and event handlers from modal markup.
---

# Modal Refactor (правила проекта)

## Overview

Одна модалка — один изолированный Thymeleaf-фрагмент. Внутри одной модалки используется **ровно одна**
система классов: либо Bootstrap, либо собственный БЭМ. Стили модалок живут в одном файле.

Цель — чтобы модалку можно было перенести или изменить, не задевая страницы, JS и чужие стили.

## Hard rules

### 1. Расположение и структура

- Все модалки: `src/main/resources/templates/fragments/modals/`.
- Один файл = одна модалка = один `th:fragment`. Имя фрагмента = имя файла (`message`, `video`,
  `image-picker`, `mail-instruction`).
- В страницах не должно быть inline-разметки модалок. Только `<div th:replace="~{fragments/modals/... :: ...}">`.
- DOM `id` модалки уникален на странице. Дубликаты (`#myModal` в двух файлах) запрещены.

### 2. Ровно одна система классов на модалку

**Вариант A — Bootstrap.** Проект использует Bootstrap **4.4.1** (`static/js/bootstrap.min.js`):

- оболочка: `modal fade` → `modal-dialog` → `modal-content` → `modal-header` / `modal-body` / `modal-footer`;
- заголовок: `modal-title`; закрытие: `class="close"` + `data-dismiss="modal"`;
- триггеры: `data-toggle="modal"` + `data-target="#id"`;
- **нельзя** `data-bs-*`, `btn-close`, `modal-dialog-centered`-из-BS5 и прочие атрибуты BS5, пока
  загружен BS 4.4.1. Обновление Bootstrap — отдельная задача.

**Вариант B — кастомный БЭМ.**

- блок: `.custom-modal`; элементы: `__dialog`, `__content`, `__header`, `__title`, `__close`, `__body`,
  `__footer`; состояние: `.custom-modal--open`.
- Никаких `modal`, `modal-header`, `fade`, `btn-close` на этих элементах.

**Запрещено смешивать:** `modal fade custom-modal`, `modal-header custom-modal__header`,
`class="delete-button btn-close-custom"` на кнопке закрытия, кастомные классы на элементах оболочки
Bootstrap. Классы **контента** внутри `__body`/`modal-body` могут быть своими, но не должны переопределять
геометрию и оформление оболочки.

Не использовать несуществующий тег `h7` — только `h5`/`h4`.

### 3. Стили

- Единственное место для стилей модалок: `src/main/resources/static/css/components/modal.css`
  (уже импортируется в `main.css`).
- Никаких `style="..."` в разметке модалки и на кнопках-триггерах. Никаких `element.style.*` из JS для
  состояния модалки — только класс (например, `body.modal-open`).
- Правила модалок из `pages/video.css`, `pages/mail.css`, `responsetive/*.css` переносятся в `modal.css`
  вместе с медиа-запросами. Мёртвые селекторы удаляются.

### 4. JS-контракт

- Никаких inline-обработчиков (`onclick`, `onsubmit`) в разметке модалки.
- Bootstrap-модалки открываются/закрываются data-атрибутами BS4; кастомные — модулем по
  `data-modal-open` / `data-modal-close`.
- Селекторы в JS скоупятся к id модалки (`#myModal .modal-body`), а не глобально (`.modal-body`).
- Сохранять id/классы, которые используют UI-тесты, или обновлять page object в том же изменении
  (`AdminPage`: `#myModal`, `.modal-body .text-success`, `.modal-footer .btn-secondary`).

### 5. Перемещение модалки — обязательный чек-лист

Перед началом:

```bash
grep -rn "modal::" src/main/java src/main/resources/templates
grep -rn "video-modal\|mail-instruction-modal\|infoModal\|btn-close-custom" src/main/resources
```

Обновить **всё** за один шаг:

1. `th:replace` на страницах;
2. Java view-имена в контроллерах/`GlobalExceptionHandler` (там `modal::modal-body`);
3. JS-селекторы и имена классов состояния;
4. UI page object и тесты;
5. CSS (перенос правил, удаление старых);
6. только после этого удалить старый файл/фрагмент.

## Workflow

1. Обновить инвентарь в `SPEC_MODALS.md`.
2. Получить подтверждение владельца на перенос.
3. Переносить по одной модалке: разметка → include → Java/JS/тесты → CSS → проверка страниц.
4. Удалять старое только после зелёной проверки.
5. Отметить статус в `SPEC_MODALS.md`.

## Quick reference

| Файл | Фрагмент | id | Система |
|---|---|---|---|
| `fragments/modals/message.html` | `message` (+ `message-body`) | `myModal` | Bootstrap 4.4.1 |
| `fragments/modals/video.html` | `video` | `videoModal` | Custom BEM (`--video`) |
| `fragments/modals/image-picker.html` | `image-picker` | `infoModal` | Bootstrap 4.4.1 |
| `fragments/modals/mail-instruction.html` | `mail-instruction` | `mailSettingsModal` | Custom BEM (`--mail`) |

## Common mistakes

- Оставить `data-bs-dismiss` при Bootstrap 4 — кнопка закрытия не работает.
- Добавить `class="modal fade"` к кастомной модалке «для совместимости» — смешивание систем.
- Перенести файл, но забыть `modal::modal-body` в Java — ошибка рендеринга на ошибках формы.
- Оставить глобальный селектор `.modal-body` в `forms.js` — после переноса он может попасть в чужую модалку.
- Стилизовать модалку из `pages/*.css` — правило «всё в `components/modal.css`» нарушено.
- Удалить `#myModal`, не обновив UI-тесты.

## Definition of done

```bash
# 1. модалки только в fragments/modals
grep -rn 'class="[^"]*modal' src/main/resources/templates --include=*.html

# 2. нет inline-стилей в модалках
grep -rn 'style=' src/main/resources/templates/fragments/modals

# 3. нет старых view-имён
grep -rn 'modal::' src/main/java src/main/resources/templates

# 4. нет старых классов/стилей
grep -rn 'video-modal\|mail-instruction-modal\|btn-close-custom\|delete-button' src/main/resources
```

Плюс ручная проверка: `/admin` (все вкладки), `/lk`, `/reviews`, `/blog`, `/` (видео), вкладка
«Почта» в админке; закрытие кнопкой, `Esc`, кликом по фону; блокировка скролла; мобильная ширина.
