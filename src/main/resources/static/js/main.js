/* ============================================================
   ACTIVITAR - MAIN JAVASCRIPT (ТОЧКА ВХОДА)
   ============================================================ */

'use strict';

// ============================================================
// 1. ИНИЦИАЛИЗАЦИЯ ПРИ ЗАГРУЗКЕ
// ============================================================

$(window).on('load', function() {
    // Preloader
    $(".loader").fadeOut();
    $("#preloder").delay(200).fadeOut("slow");

    // Admin tabs
    $('#tabOption button').click(function(e) {
        e.preventDefault();
        $(this).tab('show');
    });

    // Gallery filter
    $('.gallery-controls ul li').on('click', function() {
        $('.gallery-controls ul li').removeClass('active');
        $(this).addClass('active');
    });

    // MixItUp gallery
    if ($('.gallery-filter').length > 0) {
        var containerEl = document.querySelector('.gallery-filter');
        var mixer = mixitup(containerEl);
    }

    // Masonry blog grid
    $('.blog-gird').masonry({
        itemSelector: '.grid-item',
        columnWidth: '.grid-sizer',
    });

});

// ============================================================
// 2. ИНИЦИАЛИЗАЦИЯ ПРИ READY
// ============================================================

$(document).ready(function() {
    console.log('🚀 Activitar - инициализация');

    initStickyHeader();
    initBackgrounds();
    initMobileMenu();
    initOwlCarousels();
    initMagnificPopup();
    initNiceSelect();
    initTimetableFilter();
    initVideoCarousel();
});
