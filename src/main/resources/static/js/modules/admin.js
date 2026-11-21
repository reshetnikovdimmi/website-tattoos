/* ============================================================
   ADMIN MODULE - Админ-панель
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * Переинициализация фонов
     */
    function reinitSetBg() {
        $('.set-bg').each(function() {
            var bg = $(this).data('setbg');
            if (bg && $(this).css('background-image') !== 'url("' + bg + '")') {
                $(this).css('background-image', 'url(' + bg + ')');
            }
        });
    }

    /**
     * Обновление фрагмента админки
     * C2 FIX: добавлена обработка ошибок
     */
    function updateAdminFragment(href) {
        $.get(href, {}, function(data) {
            $(".tab-content").html(data);
            if (window.CarouselModule) {
                CarouselModule.refresh('.tab-content');
            }
            reinitSetBg();
        }).fail(function(xhr, status, error) {
            console.error('Ошибка загрузки фрагмента:', status, error);
            $(".tab-content").html('<div class="alert alert-danger">Ошибка загрузки фрагмента</div>');
        });
    }

    /**
     * Переинициализация после обновления фрагмента
     */
    function reinitAfterFragmentUpdate(containerSelector) {
        setTimeout(function() {
            if (window.CarouselModule) {
                CarouselModule.refresh('.tab-content');
            }
            if (containerSelector && (
                containerSelector.includes('contact') ||
                containerSelector.includes('setting') ||
                containerSelector.includes('video')
            )) {
                if ($('#admin-contact-map').length) {
                    loadYandexAPI(initAdminMap);
                }
                if ($('.video-carousel').length) {
                    reinitVideoCarousel();
                }
            }
            reinitSetBg();
        }, 500);
    }

    /**
     * Удаление слайда карусели (без inline onclick)
     */
    $(document).on('click', '[data-carousel-delete]', function(e) {
        e.preventDefault();
        if (typeof window.deleteImage === 'function') {
            window.deleteImage(this);
        }
    });

    // Экспорт в глобальную область
    window.AdminModule = {
        updateAdminFragment: updateAdminFragment,
        reinitSetBg: reinitSetBg,
        reinitAfterFragmentUpdate: reinitAfterFragmentUpdate
    };

    // Глобальные функции для HTML onclick
    window.updateAdminFragment = updateAdminFragment;
    window.reinitSetBg = reinitSetBg;
    window.reinitAfterFragmentUpdate = reinitAfterFragmentUpdate;
})(window);
