/**
 * Админские операции галереи: удаление, флаг, пагинация модального выбора.
 * Пагинация и фильтры страниц живут в modules/pagination.js.
 */
(function (window, $) {
    'use strict';

    if (!$) {
        return;
    }

    /**
     * Удаление изображения/эскиза.
     * Кнопка должна содержать data-deletion-url, data-fragment-name,
     * data-container-selector; id элемента — id сущности.
     */
    function deleteImage(buttonElement) {
        var imageId = buttonElement.id;
        var fragmentName = buttonElement.dataset.fragmentName;
        var containerSelector = buttonElement.dataset.containerSelector;
        var deletionUrl = buttonElement.dataset.deletionUrl;
        var method = buttonElement.dataset.method || 'POST';

        if (!deletionUrl) {
            console.error('data-deletion-url не указан');
            if (typeof window.showModalMessage === 'function') {
                window.showModalMessage('❌ Ошибка: URL удаления не указан', 'error');
            }
            return;
        }

        if (!window.confirm(buttonElement.dataset.confirmMessage || 'Вы действительно хотите удалить этот элемент?')) {
            return;
        }

        var $button = $(buttonElement);
        var originalHtml = $button.html();

        $button.html('<i class="fa fa-spinner fa-spin"></i> Удаление...');
        $button.prop('disabled', true);

        var url = deletionUrl + '/' + imageId;
        if (fragmentName) {
            url += '?fragment=' + encodeURIComponent(fragmentName);
        }

        var csrfToken = $('meta[name="_csrf"]').attr('content');

        $.ajax({
            url: url,
            type: method,
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                ...(csrfToken ? {'X-CSRF-TOKEN': csrfToken} : {})
            },
            success: function (response) {
                if (containerSelector && $(containerSelector).length) {
                    $(containerSelector).html(response);
                    if (typeof window.reinitSetBg === 'function') {
                        window.reinitSetBg();
                    }
                    if (typeof window.reinitAfterFragmentUpdate === 'function') {
                        window.reinitAfterFragmentUpdate(containerSelector);
                    }
                }
                if (typeof window.showModalMessage === 'function') {
                    window.showModalMessage('✅ Элемент успешно удален!', 'success');
                }
            },
            error: function (xhr) {
                console.error('Ошибка при удалении:', xhr);
                if (typeof window.modals === 'function') {
                    window.modals();
                }
                $('.modal-body').html(xhr.responseText);
            },
            complete: function () {
                if ($button.closest('.user-card, .item-card, .image-card').length) {
                    $button.html(originalHtml);
                    $button.prop('disabled', false);
                }
            }
        });
    }

    /**
     * Пагинация модального окна выбора изображения для отзывов.
     */
    function goToReviewsPage(page, size) {
        $.get('/gallery/reviews/' + page + '/' + size, {}, function (data) {
            $('.modal-img').html(data);
        });
    }

    $(document).on('click', '[data-delete-item]', function (event) {
        event.preventDefault();
        deleteImage(this);
    });

    $(document).on('change', '[data-featured]', function () {
        if (typeof window.toggleFeatured === 'function') {
            window.toggleFeatured(this);
        }
    });

    $(document).on('click', '[data-review-page]', function (event) {
        event.preventDefault();
        var page = parseInt($(this).attr('data-review-page'), 10) || 0;
        var size = parseInt($(this).attr('data-review-size'), 10) || 9;
        goToReviewsPage(page, size);
    });

    $(document).on('change', '[data-review-size]', function () {
        var page = parseInt($(this).attr('data-review-page'), 10) || 0;
        goToReviewsPage(page, parseInt(this.value, 10) || 9);
    });

    window.GalleryModule = {
        deleteImage: deleteImage,
        goToReviewsPage: goToReviewsPage
    };
    window.deleteImage = deleteImage;
}(window, window.jQuery));
