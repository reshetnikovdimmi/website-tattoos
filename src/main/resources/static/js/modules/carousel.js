/* ============================================================
   CAROUSEL MODULE - Unified Controller
   Owl Carousel (public) + Bootstrap Carousel (admin)
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * ==========================================================
     * Owl Carousel Controller (public hero carousel)
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
     * Bootstrap Carousel Controller (admin carousel)
     * ==========================================================
     */

    /**
     * Перейти на слайд
     */
    function goToSlide(slideIndex) {
        var $carousel = $('#adminCarousel');
        
        // Bootstrap 4/5 native method
        if ($carousel.data('bs.carousel')) {
            $carousel.data('bs.carousel').to(slideIndex);
        } 
        // Legacy Bootstrap (3.x) or manual fallback
        else if ($carousel.data('carousel')) {
            $carousel.carousel(slideIndex);
        } 
        // Fallback: manual class manipulation
        else {
            var $items = $carousel.find('.carousel-item');
            $items.removeClass('active').eq(slideIndex).addClass('active');
            $carousel.find('.carousel-indicators button').removeClass('active').eq(slideIndex).addClass('active');
        }
    }

    /**
     * Предыдущий слайд
     */
    function carouselPrev() {
        var $carousel = $('#adminCarousel');
        
        if ($carousel.data('bs.carousel')) {
            $carousel.data('bs.carousel').prev();
        } else if ($carousel.data('carousel')) {
            $carousel.carousel('prev');
        } else {
            var $items = $carousel.find('.carousel-item');
            var currentIndex = $items.index($items.filter('.active'));
            var newIndex = (currentIndex - 1 + $items.length) % $items.length;
            goToSlide(newIndex);
        }
    }

    /**
     * Следующий слайд
     */
    function carouselNext() {
        var $carousel = $('#adminCarousel');
        
        if ($carousel.data('bs.carousel')) {
            $carousel.data('bs.carousel').next();
        } else if ($carousel.data('carousel')) {
            $carousel.carousel('next');
        } else {
            var $items = $carousel.find('.carousel-item');
            var currentIndex = $items.index($items.filter('.active'));
            var newIndex = (currentIndex + 1) % $items.length;
            goToSlide(newIndex);
        }
    }

    /**
     * ==========================================================
     * Public API
     * ==========================================================
     */
    window.CarouselModule = {
        // Owl Carousel
        initOwlCarousel: initOwlCarousel,
        
        // Bootstrap Carousel (admin)
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
