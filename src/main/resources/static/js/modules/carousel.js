/* ============================================================
   CAROUSEL MODULE - Unified Controller
   Owl Carousel (public hero + admin preview)
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * ==========================================================
     * Owl Carousel Controller (public hero + admin)
     * ==========================================================
     */

    /**
     * Инициализация Owl Carousel
     */
    function initOwlCarousel(selector, options) {
        var $carousel = $(selector);
        if (!$carousel.length || $carousel.hasClass('owl-initialized')) return;

        var defaults = {
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
        };

        $carousel.owlCarousel($.extend(defaults, options));
    }

    /**
     * ==========================================================
     * Navigation helpers (Owl Carousel API)
     * ==========================================================
     */

    /**
     * Перейти на слайд (Owl Carousel)
     */
    function goToSlide(slideIndex) {
        var $carousel = $('#adminCarousel');
        if ($carousel.data('owl.carousel')) {
            $carousel.data('owl.carousel').to(slideIndex, 400);
        }
    }

    /**
     * Предыдущий слайд (Owl Carousel)
     */
    function carouselPrev() {
        var $carousel = $('#adminCarousel');
        if ($carousel.data('owl.carousel')) {
            $carousel.data('owl.carousel').prev();
        }
    }

    /**
     * Следующий слайд (Owl Carousel)
     */
    function carouselNext() {
        var $carousel = $('#adminCarousel');
        if ($carousel.data('owl.carousel')) {
            $carousel.data('owl.carousel').next();
        }
    }

    /**
     * ==========================================================
     * Public API
     * ==========================================================
     */
    window.CarouselModule = {
        initOwlCarousel: initOwlCarousel,
        goToSlide: goToSlide,
        carouselPrev: carouselPrev,
        carouselNext: carouselNext
    };

    /**
     * ==========================================================
     * Global functions for HTML onclick handlers
     * ==========================================================
     */
    window.goToSlide = goToSlide;
    window.carouselPrev = carouselPrev;
    window.carouselNext = carouselNext;

})(window);
