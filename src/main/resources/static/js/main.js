/* ============================================================
   ACTIVITAR - MAIN JAVASCRIPT
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
    initYandexMap();
});

// ============================================================
// 2. UI КОМПОНЕНТЫ
// ============================================================

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

// ============================================================
// 3. ВИДЕО КАРУСЕЛЬ
// ============================================================

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

function reinitVideoCarousel() {
    setTimeout(initVideoCarousel, 300);
}

// ============================================================
// 4. ВИДЕО МОДАЛЬНОЕ ОКНО
// ============================================================

$(document).ready(function() {
    const modal = document.getElementById('videoModal');
    if (!modal) return;

    const modalIframe = document.getElementById('videoModalIframe');
    const modalTitle = document.getElementById('videoModalTitle');
    const closeBtn = document.getElementById('videoModalClose');

    $(document).on('click', '.video-card', function() {
        const videoUrl = $(this).data('video-url');
        const title = $(this).find('.video-card-info h5').text() || 'Видео';

        if (videoUrl) {
            modalIframe.src = videoUrl + '&autoplay=1';
            modalTitle.textContent = title;
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    });

    function closeVideoModal() {
        modal.classList.remove('active');
        modalIframe.src = '';
        document.body.style.overflow = '';
    }

    if (closeBtn) closeBtn.addEventListener('click', closeVideoModal);
    modal.addEventListener('click', function(e) {
        if (e.target === this) closeVideoModal();
    });
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && modal.classList.contains('active')) {
            closeVideoModal();
        }
    });
});

// ============================================================
// 6. АДМИН-ПАНЕЛЬ - ОБЩИЕ ФУНКЦИИ
// ============================================================

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
 */
function updateAdminFragment(href) {
    $.get(href, {}, function(data) {
        $(".tab-content").html(data);
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

// ============================================================
// 7. ФОРМЫ - SUBMIT
// ============================================================

async function submitForm(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const formAction = form.action;
    const button = form.querySelector('button[type="submit"]');
    const inputs = form.querySelectorAll('input, textarea');
    const originalText = button.textContent;

    if (!form) {
        console.error('Форма не найдена');
        return;
    }

    const { fragmentName, containerSelector } = getFragmentInfo(form);

    // Режим редактирования
    if (button.textContent.trim().toLowerCase() === 'изменить') {
        inputs.forEach(input => input.disabled = false);
        button.textContent = 'Сохранить';
        form.classList.add('active');
        $(button).html('<i class="fa fa-save"></i> ' + button.textContent);
        return;
    }

    const formData = new FormData(form);
    if (fragmentName) {
        formData.append('fragment', fragmentName);
    }

   if (event.target.id) {
          formData.append('id', event.currentTarget.id);
      }


    button.disabled = true;
    button.textContent = 'Сохранение...';

    try {
        const response = await $.ajax({
            url: formAction,
            dataType: 'html',
            cache: false,
            contentType: false,
            processData: false,
            data: formData,
            type: 'POST'
        });

        console.log('✅ Ответ получен');

        showModalMessage('✅ SUCCESS!');

        if (response && containerSelector && $(containerSelector).length) {
            $(containerSelector).html(response);
            reinitSetBg();
            reinitAfterFragmentUpdate(containerSelector);
        }

        button.textContent = 'Изменить';
        inputs.forEach(input => input.disabled = true);
        button.disabled = false;
        form.classList.remove('active');
        $(button).html('<i class="fa fa-save"></i> ' + button.textContent);

    } catch (error) {
        console.error('❌ Ошибка:', error);
        modals();
        if (error.responseText) {
            $('.modal-body').html(error.responseText);
        }
        button.textContent = originalText;
        button.disabled = false;
    }
}

function getFragmentInfo(form) {
    // 1. data-атрибуты
    if (form.dataset.fragmentName && form.dataset.containerSelector) {
        return {
            fragmentName: form.dataset.fragmentName,
            containerSelector: form.dataset.containerSelector
        };
    }

    // 2. data-fragment с разделителем
    if (form.dataset.fragment && form.dataset.fragment.includes('|')) {
        const [fragmentName, containerSelector] = form.dataset.fragment.split('|');
        return { fragmentName, containerSelector };
    }

    // 3. Автоматический поиск
    let fragmentName = null;
    let containerSelector = null;
    let parent = form.parentElement;

    while (parent && parent !== document.body) {
        if (parent.hasAttribute('th:fragment')) {
            fragmentName = parent.getAttribute('th:fragment');
            const container = form.closest('[class*="-import"]');
            if (container) {
                const classes = container.className.split(' ');
                containerSelector = '.' + classes.find(c => c.includes('-import'));
            }
            break;
        }
        parent = parent.parentElement;
    }

    return { fragmentName, containerSelector };
}

// ============================================================
// 8. МОДАЛЬНЫЕ ОКНА
// ============================================================

function showModalMessage(message, type = 'success') {
    console.log('📢 Сообщение:', message);
    const modal = $('#myModal');
    if (!modal.length) {
        alert(message);
        return;
    }

    const modalBody = modal.find('.modal-body');
    modalBody.empty();

    const icon = type === 'success' ? '✅' : '❌';
    const messageClass = type === 'success' ? 'text-success' : 'text-danger';

    modalBody.html(`
        <div style="text-align: center; padding: 10px;">
            <div style="font-size: 48px; margin-bottom: 10px;">${icon}</div>
            <p class="${messageClass}" style="margin: 0; font-size: 16px;">${message}</p>
        </div>
    `);

    modal.modal('show');
    setTimeout(() => {
        if (modal.hasClass('show')) modal.modal('hide');
    }, 3000);
}

function modals() {
    $('#myModal').modal("show");
    $('.btn-close-custom, .btn-secondary').on('click', function() {
        $('#myModal').modal('hide');
    });
    $('.btn-primary').attr('disabled', true);
}

// ============================================================
// 9. ГАЛЕРЕЯ
// ============================================================

function goToPageGalleryAdmin(style, page, number) {
    sessionStorage.setItem('activeGalleryStyle', style.trim());
    $.get(`admin/gallery/${style.trim()}/${page}/${number}`, {}, function(data) {
        $(".galleryFragment").html(data);
        document.getElementById('category').value = style.trim();
        initGalleryActiveState();
    });
}

function initGalleryActiveState() {
    var activeStyle = sessionStorage.getItem('activeGalleryStyle') || 'Вся галерея';
    $('.gallery-controls ul li').removeClass('active');
    $('.gallery-controls ul li').each(function() {
        if ($(this).text().trim() === activeStyle) {
            $(this).addClass('active');
        }
    });
}

function goToPageGallery(style, page, number) {
    $.get(`/gallery/${style.trim()}/${page}/${number}`, {}, function(data) {
        $(".galleryFilter").html(data);
    });
}

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

function goToPageGalleryReviews(page, number) {
    $.get(`/gallery/reviews/${page}/${number}`, {}, function(data) {
        $(".modal-img").html(data);
    });
}

// ============================================================
// 10. УДАЛЕНИЕ
// ============================================================

function deleteImage(buttonElement) {
    const imageId = buttonElement.id;
    const fragmentName = buttonElement.dataset.fragmentName;
    const containerSelector = buttonElement.dataset.containerSelector;
    const deletionUrl = buttonElement.dataset.deletionUrl;
    const method = buttonElement.dataset.method || 'DELETE';

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

    $.ajax({
        url: url,
        type: method,
        headers: { 'X-Requested-With': 'XMLHttpRequest' },
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

// ============================================================
// 11. ЗАГРУЗКА ИЗОБРАЖЕНИЙ
// ============================================================

function loadImage(input, targetImage) {
    if (input.files && input.files[0]) {
        const file = input.files[0];

        if (!file.type.match('image.*')) {
            showModalMessage('❌ Пожалуйста, выберите изображение', 'error');
            input.value = '';
            return;
        }

        if (file.size > 5 * 1024 * 1024) {
            showModalMessage('❌ Размер файла не должен превышать 5MB', 'error');
            input.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            targetImage.src = e.target.result;
            const fileInfo = document.getElementById('file-info-logo');
            const fileNameSpan = document.querySelector('.upload-file-name');
            if (fileInfo && fileNameSpan) {
                fileInfo.style.display = 'flex';
                fileNameSpan.textContent = file.name;
            }
        };
        reader.readAsDataURL(file);
    } else {
        targetImage.src = targetImage.getAttribute('data-default-src') || '/img/default-logo.png';
        const fileInfo = document.getElementById('file-info-logo');
        if (fileInfo) fileInfo.style.display = 'none';
    }
}

function updateLogoImage(input, targetImage) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            targetImage.src = e.target.result;
            document.getElementById('logo-header').src = e.target.result;
            document.getElementById('logo-footer').src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function updateBreadcrumbBackground(input, targetImage) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            targetImage.src = e.target.result;
            document.getElementById('breadcrumb-section').style.backgroundImage = `url('${e.target.result}')`;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// ============================================================
// 12. ОТЗЫВЫ
// ============================================================

function previewReviewImage(input) {
    if (input.files && input.files[0]) {
        const file = input.files[0];
        if (!file.type.match('image.*')) {
            alert('Пожалуйста, выберите файл изображения (JPEG, PNG, GIF)');
            input.value = '';
            return;
        }
        if (file.size > 5 * 1024 * 1024) {
            alert('Файл слишком большой. Максимальный размер: 5MB');
            input.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('reviewImage');
            const placeholder = document.getElementById('reviewPlaceholder');
            const container = document.getElementById('reviewPreviewContainer');
            if (preview && placeholder && container) {
                preview.src = e.target.result;
                preview.style.display = 'block';
                placeholder.style.display = 'none';
                container.classList.add('has-image');
            }
        };
        reader.readAsDataURL(file);
    }
}

function selectReviewImage(imageName) {
    const reviewImage = document.getElementById('reviewImage');
    const placeholder = document.getElementById('reviewPlaceholder');
    const imageNameInput = document.getElementById('reviewImageName');

    if (reviewImage && placeholder) {
        reviewImage.src = '/images/' + imageName;
        reviewImage.style.display = 'block';
        placeholder.style.display = 'none';
    }

    if (imageNameInput) imageNameInput.value = imageName;
    $('#infoModal').modal('hide');
}

function resetReviewForm() {
    const preview = document.getElementById('reviewImage');
    const container = preview?.closest('.review-image-preview');
    const placeholder = document.getElementById('review-placeholder');
    const fileInput = document.getElementById('review-image-file');
    const imageNameInput = document.getElementById('reviewImageName');
    const textarea = document.getElementById('review-comment');

    if (preview) {
        preview.src = '/img/placeholder-image.jpg';
        preview.classList.remove('image-loaded');
    }
    if (container) container.classList.remove('has-image');
    if (placeholder) placeholder.classList.remove('hidden');
    if (fileInput) fileInput.value = '';
    if (imageNameInput) imageNameInput.value = '';
    if (textarea) textarea.value = '';

    const ratingInputs = document.querySelectorAll('input[name="rating"]');
    if (ratingInputs.length > 0) {
        document.getElementById('star5').checked = true;
    }
}

function toggleFeatured(checkbox) {
    const isChecked = checkbox.checked;
    const id = checkbox.getAttribute('data-id');

    checkbox.disabled = true;
    $.ajax({
        url: '/admin/gallery/toggle-featured',
        type: 'POST',
        data: { id: id, flag: isChecked },
        success: function(html) {
            modals();
            $('.modal-body').html(html);
        },
        error: function(xhr) {
            checkbox.checked = !isChecked;
            modals();
            $('.modal-body').html(xhr.responseText);
        },
        complete: function() {
            checkbox.disabled = false;
        }
    });
}

// ============================================================
// 13. ОТПРАВКА EMAIL
// ============================================================

async function sendMail(event, fragmentPrefix) {
    event.preventDefault();
    const form = event.currentTarget;
    const formAction = form.action;
    const formData = new FormData(form);

    const nameInput = form.querySelector('[name=name]');
    const subjectInput = form.querySelector('[name=subject]');
    const messageTextarea = form.querySelector('[name=msgBody]');
    const phoneRegex = /^(\+7|8)?\d{10}$/;
    let errors = [];

    if (!nameInput.value.trim()) {
        errors.push('Имя не заполнено');
    } else if (nameInput.value.length < 2) {
        errors.push('Имя должно содержать минимум 2 символа');
    }

    if (!subjectInput.value.trim()) {
        errors.push('Телефон не указан');
    } else if (!phoneRegex.test(subjectInput.value)) {
        errors.push('Неправильный формат телефона');
    }

    if (!messageTextarea.value.trim()) {
        errors.push('Сообщение не заполнено');
    } else if (messageTextarea.value.length > 500) {
        errors.push('Длина сообщения превышает допустимый предел');
    }

    if (errors.length > 0) {
        alert(errors.join('\n'));
        return;
    }

    try {
        const response = await $.ajax({
            url: formAction,
            dataType: 'html',
            cache: false,
            contentType: false,
            processData: false,
            data: formData,
            type: 'POST',
        });

        $(fragmentPrefix).html(response);
        const statusMessageElement = $('.status-message');
        statusMessageElement.show();
        setTimeout(() => statusMessageElement.hide(), 15000);

    } catch (error) {
        $("#preloder").delay(200).fadeOut("slow");
        alert('Произошла ошибка при сохранении информации.');
    }
}

// ============================================================
// 14. ИНСТРУКЦИИ ДЛЯ ВИДЕО
// ============================================================

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

function switchInstructionTab(button, tab) {
    const container = button.closest('.instruction-content');
    container.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    button.classList.add('active');
    container.querySelectorAll('.tab-content').forEach(content => content.style.display = 'none');
    const targetContent = document.getElementById('instruction-' + tab);
    if (targetContent) targetContent.style.display = 'block';
}

// ============================================================
// 15. CAROUSEL ADMIN
// ============================================================

function goToSlide(slideIndex) {
    const $carousel = $('#adminCarousel');
    if ($carousel.data('bs.carousel') || $carousel.data('carousel')) {
        $carousel.carousel(slideIndex);
    } else {
        $carousel.find('.carousel-item').removeClass('active').eq(slideIndex).addClass('active');
        $carousel.find('.carousel-indicators button').removeClass('active').eq(slideIndex).addClass('active');
    }
}

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

// ============================================================
// 16. ИНИЦИАЛИЗАЦИЯ КАРТЫ ПРИ ЗАГРУЗКЕ
// ============================================================

$(document).ready(function() {
    if ($('#admin-contact-map').length) {
        loadYandexAPI(initAdminMap);
    }
});
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
/**
 * Быстрая проверка: только коннект к SMTP (1-2 сек)
 */
async function testSmtpConnection() {
    const form = document.querySelector('.settings-form');
    const containerSelector = form ? form.dataset.containerSelector : '.settings-body';

    if (!form) {
            console.error('Форма не найдена');
            return;
        }

    const formData = new FormData(form);

    try {
        const response = await $.ajax({
            url: '/admin/mail/test-connection',
            type: 'POST',
            dataType: 'html',
            data: formData,
            processData: false,
            contentType: false
        });

        console.log('🔌 Connection test:', response);
        showModalMessage('✅ SUCCESS!');

        $(containerSelector).html(response);

       // Читаем результат из скрытого div и показываем модалку
               setTimeout(() => {
                   const resultDiv = document.getElementById('smtp-test-result');
                   if (resultDiv) {
                       const success = resultDiv.dataset.success === 'true';
                       const error = resultDiv.dataset.error || '';

                       if (success) {
                           showModalMessage('✅ Соединение установлено');
                       } else {
                           showModalMessage('❌ ' + error);
                       }
                   }
               }, 100);

    } catch (error) {
        console.error('❌ Connection test error:', error);
        modals();
         if (error.responseText) {
             $('.modal-body').html(error.responseText);
         }
    }

}
      /**
      * Копировать логи из панели
      */
     function copyLogs() {
         const output = document.getElementById('smtp-log-output');
         if (!output) return;
         navigator.clipboard.writeText(output.textContent).then(() => {
             showModalMessage('📋 Логи скопированы');
         });
     }

     /**
      * Очистить логи
      */
     function clearLogs() {
         const output = document.getElementById('smtp-log-output');
         const badge = document.getElementById('smtp-status-badge');
         const toolbar = document.querySelector('.smtp-log-toolbar');

         if (output) output.textContent = 'Нажмите «Проверить» или «Отправить тест», чтобы увидеть логи...';
         if (badge) badge.style.display = 'none';
         if (toolbar) toolbar.style.display = 'none';
     }
     async function sendTestEmail() {
         const form = document.querySelector('.settings-form');
         const testEmailInput = document.getElementById('test-email');
         const testEmail = testEmailInput ? testEmailInput.value.trim() : '';

         const containerSelector = form ? form.dataset.containerSelector : '.settings-body';

         if (!testEmail) {
             modals();
             $('.modal-body').html('<div class="alert alert-warning"><i class="fa fa-exclamation-triangle"></i> Укажите тестовый email</div>');
             testEmailInput && testEmailInput.focus();
             return;
         }

         const formData = new FormData(form);
         formData.append('testEmail', testEmail);

         try {
             const response = await $.ajax({
                 url: '/admin/mail/test-send',
                 type: 'POST',
                 dataType: 'html',
                 data: formData,
                 processData: false,
                 contentType: false
             });

             console.log('✉️ Send test response received');

                 $(containerSelector).html(response);


             setTimeout(() => {
                 const resultDiv = document.getElementById('smtp-test-result');
                 if (resultDiv) {
                     const success = resultDiv.dataset.success === 'true';
                     const error = resultDiv.dataset.error || '';

                     if (success) {
                         showModalMessage('✅ Тестовое письмо отправлено');
                     } else {
                         showModalMessage('❌ ' + error);

                     }
                 }
             }, 100);

         } catch (error) {
             console.error('❌ Send test error:', error);
             modals();
             if (error.responseText) {
                 $('.modal-body').html(error.responseText);
             }

     }
     }
     /**
      * Инициализация формы настроек почты:
      * - автосмена порта при смене SSL/TLS
      * - вызывать после каждой перезагрузки фрагмента
      */
     function initMailSettings() {
         const sslRadio = document.getElementById('mail-ssl');
         const tlsRadio = document.getElementById('mail-tls');
         const portInput = document.getElementById('mail-port');

         // Если мы не на странице настроек почты — выходим
         if (!sslRadio || !tlsRadio || !portInput) return;

         function updatePort() {
             if (sslRadio.checked) {
                 portInput.value = 465;
             } else if (tlsRadio.checked) {
                 portInput.value = 587;
             }
         }

         // Снимаем старые обработчики (чтобы не дублировать при повторном вызове)
         sslRadio.removeEventListener('change', updatePort);
         tlsRadio.removeEventListener('change', updatePort);

         // Вешаем заново
         sslRadio.addEventListener('change', updatePort);
         tlsRadio.addEventListener('change', updatePort);

         // Устанавливаем правильный порт при инициализации
         updatePort();
     }

     /**
      * Показать/скрыть панель логов при клике на чекбокс "Отладка"
      */
     function toggleDebugPanel() {
         const debugCheckbox = document.getElementById('mail-debug');
         const logPanel = document.getElementById('smtp-log-panel');

         if (!debugCheckbox || !logPanel) return;

         logPanel.style.display = debugCheckbox.checked ? 'block' : 'none';
     }

// ============================================================
// 17. МОДАЛЬНОЕ ОКНО: ИНСТРУКЦИЯ ПО НАСТРОЙКЕ ПОЧТЫ
// ============================================================

function switchServiceTab(serviceId) {
    document.querySelectorAll('.service-tab-content').forEach(function(el) {
        el.classList.remove('active');
    });
    document.querySelectorAll('.service-tab-btn').forEach(function(el) {
        el.classList.remove('active');
    });
    var tab = document.getElementById('tab-' + serviceId);
    if (tab) tab.classList.add('active');
    var btn = document.querySelector('.service-tab-btn[data-tab="' + serviceId + '"]');
    if (btn) btn.classList.add('active');
}

function copyToClipboard(text, btn) {
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(function() {
            showCopied(btn);
        }).catch(function() {
            fallbackCopy(text, btn);
        });
    } else {
        fallbackCopy(text, btn);
    }
}

function showCopied(btn) {
    var original = btn.innerHTML;
    btn.innerHTML = '<i class="fa fa-check"></i> Готово';
    btn.classList.add('copied');
    setTimeout(function() {
        btn.innerHTML = original;
        btn.classList.remove('copied');
    }, 2000);
}

function fallbackCopy(text, btn) {
    var textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();
    try {
        document.execCommand('copy');
        showCopied(btn);
    } catch (err) {
        console.error('Copy failed', err);
    }
    document.body.removeChild(textarea);
}