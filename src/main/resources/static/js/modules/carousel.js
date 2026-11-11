/* ============================================================
   CAROUSEL MODULE - Admin Carousel
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * Перейти на слайд
     */
    function goToSlide(slideIndex) {
        const $carousel = $('#adminCarousel');
        if ($carousel.data('bs.carousel') || $carousel.data('carousel')) {
            $carousel.carousel(slideIndex);
        } else {
            $carousel.find('.carousel-item').removeClass('active').eq(slideIndex).addClass('active');
            $carousel.find('.carousel-indicators button').removeClass('active').eq(slideIndex).addClass('active');
        }
    }

    /**
     * Предыдущий слайд
     */
    function carouselPrev() {
        const $carousel = $('#adminCarousel');
        if ($carousel.data('bs.carousel') || $carousel.data('carousel')) {
            $carousel.carousel('prev');
        } else {
            const $items = $carousel.find('.carousel-item');
            let currentIndex = $items.index($items.filter('.active'));
            const newIndex = (currentIndex - 1 + $items.length) % $items.length;
            goToSlide(newIndex);
        }
    }

    /**
     * Следующий слайд
     */
    function carouselNext() {
        const $carousel = $('#adminCarousel');
        if ($carousel.data('bs.carousel') || $carousel.data('carousel')) {
            $carousel.carousel('next');
        } else {
            const $items = $carousel.find('.carousel-item');
            let currentIndex = $items.index($items.filter('.active'));
            const newIndex = (currentIndex + 1) % $items.length;
            goToSlide(newIndex);
        }
    }

    // Экспорт в глобальную область
    window.CarouselModule = {
        goToSlide: goToSlide,
        carouselPrev: carouselPrev,
        carouselNext: carouselNext
    };
})(window);
