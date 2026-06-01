
// Функция для загрузки изображения
function loadImage(input, targetImage) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        var file = input.files[0];

        // Проверка типа файла
        if (!file.type.match('image.*')) {
            showModalMessage('❌ Пожалуйста, выберите изображение', 'error');
            input.value = '';
            return;
        }

        // Проверка размера файла (например, не более 5MB)
        if (file.size > 5 * 1024 * 1024) {
            showModalMessage('❌ Размер файла не должен превышать 5MB', 'error');
            input.value = '';
            return;
        }

        reader.onload = function(e) {
            targetImage.src = e.target.result;

            // Показываем информацию о файле
            var fileInfo = document.getElementById('file-info-logo');
            var fileNameSpan = document.querySelector('.upload-file-name');

            if (fileInfo && fileNameSpan) {
                fileInfo.style.display = 'flex';
                fileNameSpan.textContent = file.name;
            }
        };

        reader.readAsDataURL(file);
    } else {
        // Если файл не выбран, показываем изображение по умолчанию
        targetImage.src = targetImage.getAttribute('data-default-src') || '/img/default-logo.png';

        // Скрываем информацию о файле
        var fileInfo = document.getElementById('file-info-logo');
        if (fileInfo) {
            fileInfo.style.display = 'none';
        }
    }
}
function updateLogoImage(input, targetImage) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            targetImage.src = e.target.result;
             const logoImageHeader = document.getElementById('logo-header');
             logoImageHeader.src = e.target.result;
             const logoImageFooter= document.getElementById('logo-footer');
             logoImageFooter.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}
function updateBreadcrumbBackground(input, targetImage) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            targetImage.src = e.target.result;
            const breadcrumbSection = document.getElementById('breadcrumb-section');
            breadcrumbSection.style.backgroundImage = `url('${e.target.result}')`;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

/**
 * Функция для удаления сущности (изображения, пользователя, и т.д.)
 * @param {HTMLElement} buttonElement - кнопка, по которой кликнули
 */
function deleteImage(buttonElement) {
    // Получаем данные из data-атрибутов кнопки
    const imageId = buttonElement.id;
    console.log(buttonElement.dataset);
    const fragmentName = buttonElement.dataset.fragmentName;
    const containerSelector = buttonElement.dataset.containerSelector;
    const deletionUrl = buttonElement.dataset.deletionUrl;
    const method = buttonElement.dataset.method || 'DELETE'; // Поддержка разных HTTP методов

    // Валидация обязательных параметров
    if (!deletionUrl) {
        console.error('data-deletion-url не указан');
        showModalMessage('❌ Ошибка конфигурации: URL удаления не указан', 'error');
        return;
    }

    if (!confirm(buttonElement.dataset.confirmMessage || "Вы действительно хотите удалить этот элемент?")) {
        return;
    }

    // Сохраняем jQuery объект и оригинальное содержимое
    const $button = $(buttonElement);
    const originalHtml = $button.html();
    const originalText = $button.text();

    // Показываем индикатор загрузки
    $button.html('<i class="fa fa-spinner fa-spin"></i> Удаление...');
    $button.prop('disabled', true);

    // Формируем URL с параметром fragment
    let url = deletionUrl + '/' + imageId;
    // ✅ Добавляем fragmentName в URL
    if (fragmentName) {
        url += '?fragment=' + encodeURIComponent(fragmentName);
    }

    console.log('Request URL:', url);

    $.ajax({
        url: url,
        type: method,
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        success: function(response) {
            // Обработка успешного ответа
           console.log(response);

           $(containerSelector).html(response);

            showModalMessage('✅ Элемент успешно удален!', 'success');
        },
        error: function(xhr, status, error) {
            console.error('Ошибка при удалении:', error);

            let errorMsg = '❌ Ошибка при удалении элемента!';
            if (xhr.status === 400) {
                errorMsg = '❌ Некорректный запрос!';
            } else if (xhr.status === 403) {
                errorMsg = '❌ У вас нет прав на удаление!';
            } else if (xhr.status === 404) {
                errorMsg = '❌ Элемент не найден!';
            } else if (xhr.status === 500) {
                errorMsg = '❌ Ошибка сервера! Попробуйте позже.';
            }

            // Если сервер вернул сообщение об ошибке
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMsg = `❌ ${xhr.responseJSON.message}`;
            } else if (xhr.responseText) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.message) errorMsg = `❌ ${response.message}`;
                } catch(e) {
                    // Игнорируем ошибку парсинга
                }
            }

            showModalMessage(errorMsg, 'error');
        },
        complete: function() {
            // Восстанавливаем кнопку, если элемент не был удален анимацией
            if ($button.closest('.user-card, .item-card, .image-card').length) {
                $button.html(originalHtml);
                $button.prop('disabled', false);
            }
        }
    });
}
