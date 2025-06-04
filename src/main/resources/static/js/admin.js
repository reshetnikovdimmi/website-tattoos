// Функция для загрузки данных формы
async function uploadHome(event, fragment) {
    event.preventDefault(); // Отмена стандартной отправки формы

    const form = event.currentTarget; // получаем форму через событие
    const formAction = form.action; // Извлекаем адрес из атрибута action
    const formData = new FormData(form); // Создаем объект FormData с полями формы

        try {
            const response = await $.ajax({
                url: formAction, // Адрес контроллера Spring MVC
                dataType: 'html', // Тип ожидаемого ответа — HTML-фрагмент
                cache: false,
                contentType: false,
                processData: false,
                data: formData,
                type: 'POST',
            });

            alert('Татуировка успешно сохранена!');
            console.log('Татуировка успешно сохранена!');
            $(fragment).html(response); // Замена текущего содержимого новым шаблоном
        } catch (error) {
            console.log('Ошибка при загрузке:', error);
            alert('Произошла ошибка при сохранении татуировки.');
        }
}
async function uploadTitle(event, fragment) {
    event.preventDefault(); // Отмена стандартной отправки формы

    const form = event.currentTarget; // получаем форму через событие
    const formAction = form.action; // Извлекаем адрес из атрибута action
    const formData = new FormData(form); // Создаем объект FormData с полями формы

    // Копируем значение заголовка в FormData ДО начала остальной логики
    const editableTitle = document.getElementById('editable-title');
    formData.append('textH2', editableTitle.innerHTML); // добавляем значение заголовка
    const editableDetails = document.getElementById('editable-details');
    formData.append('textH3', editableDetails.innerHTML); // добавляем значение заголовка

    // Добавляем id в FormData, если он передан
    if (event.target.id) {
        formData.append('id', event.currentTarget.id);
    }

    // Переключение между режимами редактирования и отображения
    const elements = form.querySelectorAll('[contenteditable]');
    elements.forEach(element => {
        element.contentEditable = !element.isContentEditable;
    });

    // Изменение текста кнопки
    const button = document.getElementById('edit-button');
    if (button.textContent.trim().toLowerCase() === 'изменить') {
        button.textContent = 'Сохранить'; // Меняем надпись на кнопке
    } else {
        button.textContent = 'Изменить'; // Возвращаемся назад

        try {
            const response = await $.ajax({
                url: formAction, // Адрес контроллера Spring MVC
                dataType: 'html', // Тип ожидаемого ответа — HTML-фрагмент
                cache: false,
                contentType: false,
                processData: false,
                data: formData,
                type: 'POST',
            });

            alert('Татуировка успешно сохранена!');
            console.log('Татуировка успешно сохранена!');
            $(fragment).html(response); // Замена текущего содержимого новым шаблоном
        } catch (error) {
            console.log('Ошибка при загрузке:', error);
            alert('Произошла ошибка при сохранении татуировки.');
        }
    }
}

// Функция для загрузки изображения
function loadImage(input, targetImage) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            targetImage.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// Функция для удаления изображения
function deleteImage(imageId, selectorToRefresh, deletionUrl) {
    if (!confirm("Вы действительно хотите удалить изображение?")) {
        return; // Отмена операции
    }

    // Отправляем AJAX-запрос на сервер для удаления изображения
    $.ajax({
        url: deletionUrl + "/" + imageId, // Используется настраиваемый URL
        type: 'GET',                      // Лучше использовать DELETE, но пока GET
        success: function(response) {
            // Обновляем контейнер с результатами
            $(selectorToRefresh).html(response);
        },
        error: function(error) {
            alert('Ошибка при обращении к серверу.');
        }
    });
}