function uploadHome(event, fragment) {
    event.preventDefault(); // Отмена стандартной отправки формы
    const form = event.target; // получаем форму через событие
    const formAction = form.action; // Извлекаем адрес из атрибута action
    const formData = new FormData(form); // Создаем объект FormData с полями формы
    $.ajax({
        url: formAction, // Адрес контроллера Spring MVC
        dataType: 'html', // Тип ожидаемого ответа — HTML-фрагмент
        cache: false,
        contentType: false,
        processData: false,
        data: formData,
        type: 'POST',
        success: function(data) {
            alert('Татуировка успешно сохранена!');
            console.log('Татуировка успешно сохранена!');
            $(fragment).html(data); // Замена текущего содержимого новым шаблоном
            form.reset(); // Очищаем форму после успешной отправки
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log('Ошибка при загрузке:', errorThrown);
            alert('Произошла ошибка при сохранении татуировки.');
        }
    });
}

function loadImage(input, targetImage) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            targetImage.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

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
function uploadSection(id, event, fragment) {
    event.preventDefault(); // Отмена стандартной отправки формы
    const form = event.target// получаем форму через событие
    var elements = form.querySelectorAll('[contenteditable]');
    const formData = new FormData(form); // Создаем объект FormData с полями формы
     // Добавляем id в FormData
    formData.append('id', id);
    const formAction = form.action; // Извлекаем адрес из атрибута action
    // Переключение между режимами редактирования и отображения
    elements.forEach(function(element) {
        if (element.isContentEditable) {
            element.contentEditable = false;
        } else {
            element.contentEditable = true;
        }
    });

    // Изменение текста кнопки
    if (event.target.textContent  === 'Изменить') {
        event.target.textContent  = 'Сохранить';
    } else {
              $.ajax({
                  url: formAction, // Адрес контроллера Spring MVC
                  dataType: 'html', // Тип ожидаемого ответа — HTML-фрагмент
                  cache: false,
                  contentType: false,
                  processData: false,
                  data: formData,
                  type: 'POST',
                  success: function(data) {
                      alert('Татуировка успешно сохранена!');
                      console.log('Татуировка успешно сохранена!');
                      $(fragment).html(data); // Замена текущего содержимого новым шаблоном
                      event.target.textContent = 'Изменить';
                  },
                  error: function(jqXHR, textStatus, errorThrown) {
                      console.log('Ошибка при загрузке:', errorThrown);
                      alert('Произошла ошибка при сохранении татуировки.');
                  }
              });

    }
}