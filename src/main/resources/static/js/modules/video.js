/* ============================================================
   VIDEO MODULE - Видео карусель и модальное окно
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * Инициализация видео карусели
     */
    function initVideoCarousel() {
        const $carousel = $('.video-carousel');
        if ($carousel.length === 0) return;

        const videoCount = $carousel.find('.video-card').length;
        if (videoCount === 0) {
            $carousel.hide();
            return;
        }

        $carousel.show();
        const enableLoop = videoCount > 1;

        if ($carousel.data('owl.carousel')) {
            $carousel.trigger('destroy.owl.carousel');
            $carousel.removeClass('owl-loaded');
            $carousel.find('.owl-stage-outer, .owl-stage, .owl-item').remove();
            $carousel.find('.owl-nav, .owl-dots').remove();
        }

        $carousel.owlCarousel({
            loop: enableLoop,
            margin: 20,
            nav: enableLoop,
            dots: true,
            autoplay: videoCount > 1,
            autoplayTimeout: 5000,
            autoplayHoverPause: true,
            responsive: {
                0: { items: 1, nav: false, dots: true },
                576: { items: Math.min(2, videoCount), nav: false, dots: true },
                992: { items: Math.min(3, videoCount), nav: enableLoop, dots: true },
                1200: { items: Math.min(3, videoCount), nav: enableLoop, dots: true }
            },
            navText: ['<i class="fa fa-chevron-left"></i>', '<i class="fa fa-chevron-right"></i>']
        });

        console.log('🎬 Видео-карусель инициализирована, видео:', videoCount);
    }

    /**
     * Переинициализация видео карусели
     */
    function reinitVideoCarousel() {
        setTimeout(initVideoCarousel, 300);
    }

    /**
     * Видео модальное окно
     */
    function initVideoModal() {
        const modal = document.getElementById('videoModal');
        if (!modal) return;

        const modalIframe = document.getElementById('videoModalIframe');
        const modalTitle = document.getElementById('videoModalTitle');
        const closeBtn = document.getElementById('videoModalClose');

        $(document).on('click', '.video-card', function() {
            const videoUrl = $(this).data('video-url');
            const title = $(this).find('.video-card-info h5').text() || 'Видео';

            if (videoUrl) {
                modalIframe.src = videoUrl;
                modalTitle.textContent = title;
                modal.classList.add('custom-modal--open');
                document.body.classList.add('modal-open');
            }
        });

        function closeVideoModal() {
            modal.classList.remove('custom-modal--open');
            modalIframe.src = '';
            document.body.classList.remove('modal-open');
        }

        if (closeBtn) closeBtn.addEventListener('click', closeVideoModal);
        modal.addEventListener('click', function(e) {
            if (e.target === this) closeVideoModal();
        });
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && modal.classList.contains('custom-modal--open')) {
                closeVideoModal();
            }
        });
    }

    /**
     * Показать/скрыть инструкцию для видео
     */
    function toggleInstruction(button) {
        const content = button.closest('.video-url-instruction').querySelector('.instruction-content');
        if (content.style.display === 'none' || content.style.display === '') {
            content.style.display = 'block';
            button.innerHTML = '<i class="fa fa-question-circle"></i> Скрыть инструкцию';
        } else {
            content.style.display = 'none';
            button.innerHTML = '<i class="fa fa-question-circle"></i> Как получить ссылку для встраивания?';
        }
    }

    /**
     * Переключение вкладки инструкции
     */
    function switchInstructionTab(button, tab) {
        const container = button.closest('.instruction-content');
        container.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
        button.classList.add('active');
        container.querySelectorAll('.tab-content').forEach(content => content.style.display = 'none');
        const targetContent = document.getElementById('instruction-' + tab);
        if (targetContent) targetContent.style.display = 'block';
    }

    // Экспорт в глобальную область
    window.VideoModule = {
        initVideoCarousel: initVideoCarousel,
        reinitVideoCarousel: reinitVideoCarousel,
        initVideoModal: initVideoModal,
        toggleInstruction: toggleInstruction,
        switchInstructionTab: switchInstructionTab
    };

    // Глобальные функции для HTML onclick
    window.initVideoCarousel = initVideoCarousel;
    window.reinitVideoCarousel = reinitVideoCarousel;
    window.toggleInstruction = toggleInstruction;
    window.switchInstructionTab = switchInstructionTab;
})(window);

// Инициализация видео модалки при document.ready
$(document).ready(function() {
    if (typeof VideoModule !== 'undefined' && VideoModule.initVideoModal) {
        VideoModule.initVideoModal();
    }
});
