/* ============================================================
   UI MODULE - UI компоненты
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * Липкий хедер при скролле
     */
    function initStickyHeader() {
        $(window).on('scroll', function() {
            if ($(window).scrollTop() > 50) {
                $('.header-section').addClass('scrolled');
            } else {
                $('.header-section').removeClass('scrolled');
            }
        });
    }

    /**
     * Фоновые изображения
     */
    function initBackgrounds() {
        $('.set-bg').each(function() {
            var bg = $(this).data('setbg');
            $(this).css('background-image', 'url(' + bg + ')');
        });
    }

    /**
     * Мобильное меню
     */
    function initMobileMenu() {
        // SlickNav
        $(".mobile-menu").slicknav({
            prependTo: '#mobile-menu-wrap',
            allowParentLinks: true
        });

        // Кастомное мобильное меню (сайдбар)
        $(document).on('click', '.mobile-menu-toggle-icon', function(e) {
            e.preventDefault();
            $('.mobile-menu-sidebar').addClass('active');
            $('.mobile-menu-overlay').addClass('active');
            $('body').addClass('menu-open');
        });

        $(document).on('click', '.mobile-menu-close, .mobile-menu-overlay', function() {
            $('.mobile-menu-sidebar').removeClass('active');
            $('.mobile-menu-overlay').removeClass('active');
            $('body').removeClass('menu-open');
        });

        $(document).on('keyup', function(e) {
            if (e.key === 'Escape' && $('.mobile-menu-sidebar').hasClass('active')) {
                $('.mobile-menu-sidebar').removeClass('active');
                $('.mobile-menu-overlay').removeClass('active');
                $('body').removeClass('menu-open');
            }
        });
    }

    /**
     * Owl Carousels
     */
    function initOwlCarousels() {
        // Hero Slider
        $(".hero-items").owlCarousel({
            loop: true,
            margin: 0,
            nav: true,
            items: 1,
            dots: true,
            animateOut: 'fadeOut',
            animateIn: 'fadeIn',
            navText: ['<i class="arrow_carrot-left"></i>', '<i class="arrow_carrot-right"></i>'],
            smartSpeed: 1200,
            autoHeight: false,
        });

        // Testimonial Slider
        $(".testimonial-slider").owlCarousel({
            loop: true,
            margin: 0,
            nav: false,
            items: 1,
            dots: true,
            smartSpeed: 1200,
            autoHeight: false,
            autoplay: true,
        });
    }

    /**
     * Magnific Popup
     */
    function initMagnificPopup() {
        $('.video-popup').magnificPopup({ type: 'iframe' });
        $('.image-popup').magnificPopup({ type: 'image' });
    }

    /**
     * Nice Select
     */
    function initNiceSelect() {
        $('.show-result-select:not(.no-nice-select)').niceSelect();
    }

    /**
     * Timetable Filter
     */
    function initTimetableFilter() {
        $('.timetable-controls ul li').on('click', function() {
            var tsfilter = $(this).data('tsfilter');
            $('.timetable-controls ul li').removeClass('active');
            $(this).addClass('active');

            if (tsfilter == 'all') {
                $('.classtime-table').removeClass('filtering');
                $('.ts-item').removeClass('show');
            } else {
                $('.classtime-table').addClass('filtering');
            }

            $('.ts-item').each(function() {
                $(this).removeClass('show');
                if ($(this).data('tsmeta') == tsfilter) {
                    $(this).addClass('show');
                }
            });
        });
    }

    /**
     * Изменение порта
     */
    function changePort(delta) {
        const input = document.getElementById('mail-port');
        if (!input) return;

        let value = parseInt(input.value) || 0;
        const min = parseInt(input.min) || 1;
        const max = parseInt(input.max) || 9999;

        value += delta;

        // Ограничиваем значение
        if (value < min) value = min;
        if (value > max) value = max;

        input.value = value;

        // Анимация
        input.classList.remove('changed');
        void input.offsetWidth; // триггер перерисовки
        input.classList.add('changed');

        // Обновляем протокол (если есть)
        updateProtocol(value);
    }

    /**
     * Обновление отображения протокола
     */
    function updateProtocol(port) {
        const protocolDisplay = document.getElementById('port-protocol');
        if (!protocolDisplay) return;

        let protocol = 'SMTP';
        let color = '#6c757d';

        if (port == 465) {
            protocol = 'SSL';
            color = '#e4381c';
        } else if (port == 587) {
            protocol = 'TLS';
            color = '#ffc107';
        }

        protocolDisplay.textContent = protocol;
        protocolDisplay.style.color = color;
    }

    // Инициализация при загрузке
    document.addEventListener('DOMContentLoaded', function() {
        const input = document.getElementById('mail-port');
        if (input) {
            updateProtocol(input.value);

            // Обновляем при ручном вводе
            input.addEventListener('change', function() {
                updateProtocol(this.value);
            });
        }
    });

    // Экспорт в глобальную область
    window.UiModule = {
        initStickyHeader: initStickyHeader,
        initBackgrounds: initBackgrounds,
        initMobileMenu: initMobileMenu,
        initOwlCarousels: initOwlCarousels,
        initMagnificPopup: initMagnificPopup,
        initNiceSelect: initNiceSelect,
        initTimetableFilter: initTimetableFilter,
        changePort: changePort,
        updateProtocol: updateProtocol
    };

    // Глобальные функции для HTML onclick/onchange
    window.initStickyHeader = initStickyHeader;
    window.initBackgrounds = initBackgrounds;
    window.initMobileMenu = initMobileMenu;
    window.initOwlCarousels = initOwlCarousels;
    window.initMagnificPopup = initMagnificPopup;
    window.initNiceSelect = initNiceSelect;
    window.initTimetableFilter = initTimetableFilter;
    window.changePort = changePort;
    window.updateProtocol = updateProtocol;
})(window);
