/* ============================================================
   GALLERY MODULE - Галерея и эскизы
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * Пагинация галереи админки
     */
    function goToPageGalleryAdmin(style, page, number) {
        sessionStorage.setItem('activeGalleryStyle', style.trim());
        $.get(`admin/gallery/${style.trim()}/${page}/${number}`, {}, function(data) {
            $(".galleryFragment").html(data);
            document.getElementById('category').value = style.trim();
            initGalleryActiveState();
        });
    }

    /**
     * Инициализация активного состояния галереи
     */
    function initGalleryActiveState() {
        var activeStyle = sessionStorage.getItem('activeGalleryStyle') || 'Вся галерея';
        $('.gallery-controls ul li').removeClass('active');
        $('.gallery-controls ul li').each(function() {
            if ($(this).text().trim() === activeStyle) {
                $(this).addClass('active');
            }
        });
    }

    /**
     * Пагинация галереи клиента
     */
    function goToPageGallery(style, page, number) {
        $.get(`/gallery/${style.trim()}/${page}/${number}`, {}, function(data) {
            $(".galleryFilter").html(data);
        });
    }

    /**
     * Переход на страницу галереи из элемента
     */
    function goToGalleryPageFromElement(element) {
        let size, page;
        if (element.tagName === 'SELECT') {
            size = parseInt(element.value) || 9;
            page = parseInt(element.getAttribute('data-page')) || 0;
        } else {
            size = parseInt(element.getAttribute('data-size')) || 9;
            page = parseInt(element.getAttribute('data-page')) || 0;
        }

        const style = document.querySelector('.gallery-controls ul li.active')?.textContent || 'Вся галерея';
        const url = element.getAttribute('data-url');
        const container = element.getAttribute('data-container');
        const requestUrl = `${url}/${encodeURIComponent(style.trim())}/${page}/${size}`;

        const categoryInput = document.getElementById('category');
        if (categoryInput) categoryInput.value = style.trim();

        $.get(requestUrl, {}, function(data) {
            $(container).html(data);
        });
    }

    /**
     * Пагинация эскизов
     */
    function goToPageSketches(element) {
        let size, page;
        if (element.tagName === 'SELECT') {
            size = parseInt(element.value) || 9;
            page = parseInt(element.getAttribute('data-page')) || 0;
        } else {
            size = parseInt(element.getAttribute('data-size')) || 9;
            page = parseInt(element.getAttribute('data-page')) || 0;
        }

        const url = element.getAttribute('data-url');
        const container = element.getAttribute('data-container');
        const requestUrl = `${url}/${page}/${size}`;

        $.get(requestUrl, {}, function(data) {
            $(container).html(data);
        });
    }

    /**
     * Пагинация отзывов в галерее
     */
    function goToPageGalleryReviews(page, number) {
        $.get(`/gallery/reviews/${page}/${number}`, {}, function(data) {
            $(".modal-img").html(data);
        });
    }

    /**
     * Удаление изображения
     */
    function deleteImage(buttonElement) {
        const imageId = buttonElement.id;
        const fragmentName = buttonElement.dataset.fragmentName;
        const containerSelector = buttonElement.dataset.containerSelector;
        const deletionUrl = buttonElement.dataset.deletionUrl;
        const method = buttonElement.dataset.method || 'POST';

        if (!deletionUrl) {
            console.error('data-deletion-url не указан');
            showModalMessage('❌ Ошибка: URL удаления не указан', 'error');
            return;
        }

        if (!confirm(buttonElement.dataset.confirmMessage || "Вы действительно хотите удалить этот элемент?")) {
            return;
        }

        const $button = $(buttonElement);
        const originalHtml = $button.html();

        $button.html('<i class="fa fa-spinner fa-spin"></i> Удаление...');
        $button.prop('disabled', true);

        let url = deletionUrl + '/' + imageId;
        if (fragmentName) {
            url += '?fragment=' + encodeURIComponent(fragmentName);
        }

        // H1 FIX: CSRF-токен для POST-запросов
        const csrfToken = $('meta[name="_csrf"]').attr('content');

        $.ajax({
            url: url,
            type: method,
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                ...(csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {})
            },
            success: function(response) {
                if (containerSelector && $(containerSelector).length) {
                    $(containerSelector).html(response);
                    reinitSetBg();
                    reinitAfterFragmentUpdate(containerSelector);
                }
                showModalMessage('✅ Элемент успешно удален!', 'success');
            },
            error: function(xhr) {
                console.error('Ошибка при удалении:', xhr);
                modals();
                $('.modal-body').html(xhr.responseText);
            },
            complete: function() {
                if ($button.closest('.user-card, .item-card, .image-card').length) {
                    $button.html(originalHtml);
                    $button.prop('disabled', false);
                }
            }
        });
    }

    // Экспорт в глобальную область
    window.GalleryModule = {
        goToPageGalleryAdmin: goToPageGalleryAdmin,
        initGalleryActiveState: initGalleryActiveState,
        goToPageGallery: goToPageGallery,
        goToGalleryPageFromElement: goToGalleryPageFromElement,
        goToPageSketches: goToPageSketches,
        goToPageGalleryReviews: goToPageGalleryReviews,
        deleteImage: deleteImage
    };

    // Глобальные функции для HTML onclick
    window.goToPageGalleryAdmin = goToPageGalleryAdmin;
    window.initGalleryActiveState = initGalleryActiveState;
    window.goToPageGallery = goToPageGallery;
    window.goToGalleryPageFromElement = goToGalleryPageFromElement;
    window.goToPageSketches = goToPageSketches;
    window.goToPageGalleryReviews = goToPageGalleryReviews;
    window.deleteImage = deleteImage;
})(window);
