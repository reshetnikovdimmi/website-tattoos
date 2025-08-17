
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
