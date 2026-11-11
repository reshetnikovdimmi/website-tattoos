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
        }).fail(function(xhr, status, error) {
            console.error('Ошибка загрузки фрагмента:', status, error);
            $(".tab-content").html('<div class="alert alert-danger">Ошибка загрузки фрагмента</div>');
        });
    }

    /**
     * Переинициализация после обновления фрагмента
     */
    function reinitAfterFragmentUpdate(containerSelector) {
        if (containerSelector && (
            containerSelector.includes('contact') ||
            containerSelector.includes('setting') ||
            containerSelector.includes('video')
        )) {
            setTimeout(function() {
                if ($('#admin-contact-map').length) {
                    loadYandexAPI(initAdminMap);
                }
                if ($('.video-carousel').length) {
                    reinitVideoCarousel();
                }
                reinitSetBg();
            }, 500);
        }
    }

    // Экспорт в глобальную область
    window.AdminModule = {
        updateAdminFragment: updateAdminFragment,
        reinitSetBg: reinitSetBg,
        reinitAfterFragmentUpdate: reinitAfterFragmentUpdate
    };
})(window);
