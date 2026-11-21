/* ============================================================
   CAROUSEL MODULE - единый контроллер Owl Carousel
   Публичная hero-карусель + админское превью.
   Управление: [data-carousel] + data-carousel-* атрибуты.
   ============================================================ */

(function(window) {
    'use strict';

    var DEFAULTS = {
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
        autoplay: false
    };

    function readBool($el, name, fallback) {
        var value = $el.attr('data-carousel-' + name);
        if (value === undefined) return fallback;
        return value !== 'false';
    }

    function readNumber($el, name, fallback) {
        var value = parseInt($el.attr('data-carousel-' + name), 10);
        return isNaN(value) ? fallback : value;
    }

    function readOptions($el) {
        return {
            loop: readBool($el, 'loop', DEFAULTS.loop),
            nav: readBool($el, 'nav', DEFAULTS.nav),
            dots: readBool($el, 'dots', DEFAULTS.dots),
            autoplay: readBool($el, 'autoplay', DEFAULTS.autoplay),
            smartSpeed: readNumber($el, 'speed', DEFAULTS.smartSpeed)
        };
    }

    function initOne(root) {
        var $root = $(root);
        if (!$root.length || $root.hasClass('owl-loaded') || $root.data('owl.carousel')) return;

        $root.owlCarousel($.extend({}, DEFAULTS, readOptions($root)));
    }

    function initAll(scope) {
        var $scope = scope ? $(scope) : $(document);
        if ($scope.is('[data-carousel]')) initOne($scope);
        $scope.find('[data-carousel]').each(function() {
            initOne(this);
        });
    }

    function destroyOne(root) {
        var $root = $(root);
        if (!$root.data('owl.carousel')) return;

        $root.trigger('destroy.owl.carousel');
        $root.removeClass('owl-loaded owl-drag');
        $root.removeData('owl.carousel');
        $root.find('.owl-stage-outer').children().unwrap();
    }

    function refresh(scope) {
        var $scope = scope ? $(scope) : $(document);
        var $items = $scope.is('[data-carousel]')
            ? $scope
            : $scope.find('[data-carousel]');

        $items.each(function() {
            destroyOne(this);
            initOne(this);
        });
    }

    function next(root) {
        $(root).trigger('next.owl.carousel');
    }

    function prev(root) {
        $(root).trigger('prev.owl.carousel');
    }

    function goTo(root, index) {
        $(root).trigger('to.owl.carousel', [index, 400]);
    }

    $(function() {
        initAll(document);
    });

    window.CarouselModule = {
        initAll: initAll,
        refresh: refresh,
        next: next,
        prev: prev,
        goTo: goTo
    };
})(window);
