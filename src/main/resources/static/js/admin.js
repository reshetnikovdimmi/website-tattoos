
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

// Функция для удаления изображения
function deleteImage(buttonElement) {
    // Получаем данные из data-атрибутов кнопки
    const imageId = buttonElement.id;
    const fragmentName = buttonElement.dataset.fragmentName;
    const containerSelector = buttonElement.dataset.containerSelector;
    const deletionUrl = buttonElement.dataset.deletionUrl;

    if (!confirm("Вы действительно хотите удалить этот элемент?")) {
        return;
    }

    // Показываем индикатор загрузки
    const $button = $(buttonElement);
    const originalHtml = $button.html();
    $button.html('<i class="fa fa-spinner fa-spin"></i>');
    $button.prop('disabled', true);

    // Формируем URL с параметром fragment
    let url = deletionUrl + "/" + imageId;
    if (fragmentName) {
        url += "?fragment=" + encodeURIComponent(fragmentName);
    }

    $.ajax({
        url: url,
        type: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        success: function(response) {
            if (response && containerSelector) {
                $(containerSelector).html(response);
                showModalMessage('✅ Элемент успешно удален!', 'success');
            }
        },
        error: function(xhr, status, error) {
            console.error('Ошибка при удалении:', error);
            let errorMsg = '❌ Ошибка при удалении элемента!';
            if (xhr.status === 404) {
                errorMsg = '❌ Элемент не найден!';
            } else if (xhr.status === 500) {
                errorMsg = '❌ Ошибка сервера! Попробуйте позже.';
            }
            showModalMessage(errorMsg, 'error');
        },
        complete: function() {
            $button.html(originalHtml);
            $button.prop('disabled', false);
        }
    });
}
