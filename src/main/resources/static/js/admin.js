// Функция для загрузки данных формы
async function uploadHome(event, fragment) {
    event.preventDefault(); // Отмена стандартной отправки формы

    const form = event.currentTarget; // получаем форму через событие
    const formAction = form.action; // Извлекаем адрес из атрибута action
    const formData = new FormData(form); // Создаем объект FormData с полями формы
    // Добавляем id в FormData, если он передан
        if (event.target.id) {
            formData.append('id', event.currentTarget.id);
        }
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
async function universalUploadHandler(event, fragmentPrefix) {
    event.preventDefault(); // Отмена стандартной отправки формы

    const form = event.currentTarget; // Получаем текущую форму
    const formAction = form.action; // Извлекаем адрес из атрибута action
    const formData = new FormData(form); // Создаем объект FormData с полями формы
     // Добавляем id в FormData, если он передан
        if (event.target.id) {
            formData.append('id', event.currentTarget.id);
        }

    // Изменение статуса полей и текста кнопки
    const inputs = form.querySelectorAll('input, textarea');
    const button = form.querySelector('button[type="submit"]');

    if (button.textContent.trim().toLowerCase() === 'изменить') {
        // Включаем поля для редактирования
        inputs.forEach(input => input.disabled = false);
        button.textContent = 'Сохранить'; // Меняем надпись на кнопке
        form.classList.add('active'); // Добавляем класс 'active' к форме
    } else {
        // Деактивируем поля после сохранения
        inputs.forEach(input => input.disabled = true);
        button.textContent = 'Изменить'; // Возвращаемся назад
        form.classList.remove('active'); // Убираем класс 'active' у формы
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

            alert('Информация успешно сохранена!');
            console.log('Информация успешно сохранена!');
            $(fragmentPrefix).html(response); // Замена текущего содержимого новым шаблоном
        } catch (error) {
            console.log('Ошибка при загрузке:', error);
            alert('Произошла ошибка при сохранении информации.');
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

let isEditing = false;

function toggleEditMode(event, fragmentPrefix) {
    event.preventDefault(); // Отмена стандартной отправки формы
    const form = event.currentTarget; // Получаем текущую форму
    const editButton = form.querySelector('.btn');
    const formAction = form.action; // Извлекаем адрес из атрибута action
    const inputs = form.querySelectorAll('input, textarea'); // Получаем поля только из текущей формы

    if (!isEditing) {
        // Переводим в режим редактирования
        inputs.forEach(input => input.removeAttribute('disabled')); // Активируем поля
        editButton.textContent = 'Сохранить'; // Меняем надпись на кнопке
        form.classList.add('active'); // Добавляем класс 'active' к форме
        isEditing = true;
    } else {
        // Собираем данные из полей и отправляем их на сервер
        const entry = {
            id: form.id,
            textH1: form.querySelector('[name="textH1"]')?.value || null,
            textH2: form.querySelector('[name="textH2"]')?.value || null,
            textH3: form.querySelector('[name="textH3"]')?.value || null,
            textH4: form.querySelector('[name="textH4"]')?.value || null,
            textH5: form.querySelector('[name="textH5"]')?.value || null
        };
            form.classList.remove('active'); // Убираем класс 'active' у формы
        // Отправляем данные на сервер (можете использовать Fetch API или XMLHttpRequest)
        $.ajax({
            url: formAction, // URL контроллера
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(entry),
            success: function(response) {
                alert('Данные успешно отправлены в контроллер!');
                 $(fragmentPrefix).html(response); // Замена текущего содержимого новым шаблоном
            },
            error: function(err) {
                console.error('Ошибка при отправке данных:', err.responseText);
                alert('Ошибка при отправке данных. Попробуйте снова.');
            }
        });

        // Возвращаем в режим просмотра
        inputs.forEach(input => input.setAttribute('disabled', '')); // Блокируем поля
        editButton.textContent = 'Изменить'; // Возвращаем старую надпись
        isEditing = false;
    }
}