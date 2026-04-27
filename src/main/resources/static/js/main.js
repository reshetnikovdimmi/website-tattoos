/*  ---------------------------------------------------
  Template Name: Activitar
  Description:  Activitar Fitness HTML Template
  Author: Colorlib
  Author URI: https://colorlib.com
  Version: 1.0
  Created: Colorlib
---------------------------------------------------------  */
'use strict';
(function($) {
    /*------------------
        Preloader
    --------------------*/
    $(window).on('load', function() {
        $(".loader").fadeOut();
        $("#preloder").delay(200).fadeOut("slow");
        /*------------------
            Admin tab-content
        --------------------*/

        $('#tabOption button').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
        /*------------------
            Gallery filter
        --------------------*/
        $('.gallery-controls ul li').on('click', function() {
            $('.gallery-controls ul li').removeClass('active');
            $(this).addClass('active');
        });
        if ($('.gallery-filter').length > 0) {
            var containerEl = document.querySelector('.gallery-filter');
            var mixer = mixitup(containerEl);
        }
        $('.blog-gird').masonry({
            itemSelector: '.grid-item',
            columnWidth: '.grid-sizer',
        });

    });
    /*------------------
        Background Set
    --------------------*/
    $('.set-bg').each(function() {
        var bg = $(this).data('setbg');
        $(this).css('background-image', 'url(' + bg + ')');
    });
    /*------------------
		Navigation
	--------------------*/
    $(".mobile-menu").slicknav({
        prependTo: '#mobile-menu-wrap',
        allowParentLinks: true
    });
    /*------------------
		Menu Hover
	--------------------*/
    $(".header-section .nav-menu .mainmenu ul li").on('mousehover', function() {
        $(this).addClass('active');
    });
    $(".header-section .nav-menu .mainmenu ul li").on('mouseleave', function() {
        $('.header-section .nav-menu .mainmenu ul li').removeClass('active');
    });
    /*------------------
        Carousel Slider
    --------------------*/
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
    /*------------------
        Testimonial Slider
    --------------------*/
    $(".testimonial-slider").owlCarousel({
        loop: true,
        margin: 0,
        nav: false,
        items: 1,
        dots: true,
        navText: ['<i class="arrow_carrot-left"></i>', '<i class="arrow_carrot-right"></i>'],
        smartSpeed: 1200,
        autoHeight: false,
        autoplay: true,
    });
    /*------------------
        Magnific Popup
    --------------------*/
    $('.video-popup').magnificPopup({
        type: 'iframe'
    });
    $('.image-popup').magnificPopup({
        type: 'image'
    });
    /*------------------
        Magnific Popup
    --------------------*/
    $('.show-result-select:not(.no-nice-select)').niceSelect();
    /*------------------
       Timetable Filter
    --------------------*/
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
})(jQuery);
    /*------------------
        Send Mail
    -------------------*/
    async function sendMail(event, fragmentPrefix) {
        event.preventDefault(); // Отмена стандартной отправки формы
        const form = event.currentTarget; // Получаем текущую форму
        const formAction = form.action; // Извлекаем адрес из атрибута action
        const formData = new FormData(form); // Создаем объект FormData с полями формы
        // Элементы формы
        const nameInput = form.querySelector('[name=name]');
        const subjectInput = form.querySelector('[name=subject]');
        const messageTextarea = form.querySelector('[name=msgBody]');
        // Регулярное выражение для российского формата телефона (+7XXXXXXXXXX)
        const phoneRegex = /^(\+7|8)?\d{10}$/;
        let errors = [];
        // Проверка имени
        if (!nameInput.value.trim()) {
            errors.push('Имя не заполнено');
        } else if (nameInput.value.length < 2) {
            errors.push('Имя должно содержать минимум 2 символа');
        }
        // Проверка телефона
        if (!subjectInput.value.trim()) {
            errors.push('Телефон не указан');
        } else if (!phoneRegex.test(subjectInput.value)) {
            errors.push('Неправильный формат телефона');
        }
        // Проверка сообщения
        if (!messageTextarea.value.trim()) {
            errors.push('Сообщение не заполнено');
        } else if (messageTextarea.value.length > 500) {
            errors.push('Длина сообщения превышает допустимый предел');
        }
        // Если есть ошибки, выводим их и прерываем выполнение
        if (errors.length > 0) {
            alert(errors.join('\n'));
            return;
        }
        try {
            const response = await $.ajax({
                url: formAction, // Адрес контроллера Spring MVC
                dataType: 'html', // Тип ожидаемого ответа — HTML-фрагмент
                cache: false,
                contentType: false,
                processData: false,
                data: formData,
                type: 'POST',
            });
            console.log('Информация успешно сохранена!');
            $(fragmentPrefix).html(response); // Замена текущего содержимого новым шаблоном
            // Найдем элемент статуса и покажем его
            const statusMessageElement = $('.status-message');
            statusMessageElement.show(); // показываем элемент с сообщением
            // Через 30 секунд прячем элемент обратно
            setTimeout(() => {
                statusMessageElement.hide(); // скрыть элемент
            }, 15000); // таймаут в миллисекундах (15 секунд)
        } catch (error) {
            $("#preloder").delay(200).fadeOut("slow");
            alert('Произошла ошибка при сохранении информации.');
        }
    }
     /*------------------------------------
           Функции обновления админской части
        ------------------------------------*/
      function updateAdminFragment(href) {
            $.get(href, {}, function(data) {
                $(".tab-content").html(data);
            });
        }

    /*------------------
         Carousel-admin
    --------------------*/

   // Функции для управления каруселью
   function carouselPrev() {
       $('#adminCarousel').carousel('prev');
   }

   function carouselNext() {
       $('#adminCarousel').carousel('next');
   }
    /*----------------------------
    Функция для загрузки данных формы
    -----------------------------*/
    async function submitForm(event) {
    event.preventDefault();

    const form = event.currentTarget;
    const formAction = form.action;
    const button = form.querySelector('button[type="submit"]');
    const inputs = form.querySelectorAll('input, textarea');

    if (!form) {
        console.error('Форма не найдена');
        return;
    }
   // Получаем информацию о фрагменте
       const { fragmentName, containerSelector } = getFragmentInfo(form);

    // Режим редактирования
    if (button.textContent.trim().toLowerCase() === 'изменить') {
        inputs.forEach(input => input.disabled = false);
        button.textContent = 'Сохранить';
        form.classList.add('active');
        return;
    }

    // Режим сохранения
    const formData = new FormData(form);

        if (fragmentName) {
            formData.append('fragment', fragmentName);
        }

          // Добавляем id в FormData, если он передан
                if (event.target.id) {
                    formData.append('id', event.currentTarget.id);
                }

    // Сохраняем оригинальный текст кнопки
    const originalText = button.textContent;
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

        // Показываем успех в модальном окне
        showModalMessage('✅ Информация успешно сохранена!');

        // Обновляем фрагмент
        if (response && containerSelector) {
        console.log(containerSelector);
        console.log(response);
            $(containerSelector).html(response);
        }

        // Возвращаем кнопку в исходное состояние
        button.textContent = 'Изменить';
        inputs.forEach(input => input.disabled = true);
        form.classList.remove('active');

    } catch (error) {
        console.error('Ошибка:', error);

        // Показываем ошибку в модальном окне
        let errorMsg = '❌ Ошибка при сохранении';
        if (error.status === 404) {
            errorMsg = '❌ Сервер не найден. Проверьте соединение.';
        } else if (error.status === 500) {
            errorMsg = '❌ Ошибка на сервере. Попробуйте позже.';
        }

        showModalMessage(errorMsg, 'error');

        // Возвращаем кнопку
        button.textContent = originalText;
        button.disabled = false;
    }
}
/**
 * Получение данных о фрагменте
 * @param {HTMLElement} form - форма
 * @returns {Object} { fragmentName, containerSelector }
 */
function getFragmentInfo(form) {
    // 1. Приоритет - data-атрибуты
    if (form.dataset.fragmentName && form.dataset.containerSelector) {
        return {
            fragmentName: form.dataset.fragmentName,
            containerSelector: form.dataset.containerSelector
        };
    }

    // 2. Если указан только data-fragment (содержит оба значения через |)
    if (form.dataset.fragment && form.dataset.fragment.includes('|')) {
        const [fragmentName, containerSelector] = form.dataset.fragment.split('|');
        return { fragmentName, containerSelector };
    }

    // 3. Автоматический поиск
    let fragmentName = null;
    let containerSelector = null;

    // Ищем родительский th:fragment
    let parent = form.parentElement;
    while (parent && parent !== document.body) {
        if (parent.hasAttribute('th:fragment')) {
            fragmentName = parent.getAttribute('th:fragment');
            // Ищем контейнер по классу с -import
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

// Функция показа сообщения в модальном окне
    function showModalMessage(message, type = 'success') {
        console.log('Показываем сообщение:', message);

        const modal = $('#myModal');

        if (!modal.length) {
            console.error('Модальное окно не найдено!');
            alert(message);
            return;
        }

        // Получаем modal-body и очищаем его
        const modalBody = modal.find('.modal-body');
        modalBody.empty();

        // Создаем элемент для сообщения
        const messageClass = type === 'success' ? 'text-success' : 'text-danger';
        const messageHtml = `<p class="${messageClass}" style="text-align: center; margin: 0;">${message}</p>`;
        modalBody.html(messageHtml);

        // Показываем модальное окно
        modal.modal('show');

        // Автоматически закрываем через 3 секунды
        setTimeout(() => {
            if (modal.hasClass('show')) {
                modal.modal('hide');
            }
        }, 3000);
    }
    /*---------------
    Gallery controls
    --------------*/
 function goToPageGalleryAdmin(style, page, number) {
        $.get(`admin/gallery/${style.trim()}/${page}/${number}`, {}, function(data) {
            $(".galleryFragment").html(data);

            document.getElementById('category').value = style.trim();
        });
    }
    function goToPageGallery(style, page, number) {
        $.get(`/gallery/${style.trim()}/${page}/${number}`, {}, function(data) {
            $(".galleryFilter").html(data);
        });
    }
function goToGalleryPageFromElement(element) {
    let size, page;

    // Проверяем, является ли элемент селектом (select)
    if (element.tagName === 'SELECT') {
        // Берем значение из выбранной опции
        size = parseInt(element.value) || 9;
        // Берем page из data-атрибута селекта
        page = parseInt(element.getAttribute('data-page')) || 0;
    } else {
        // Если это не селект, берем из атрибутов как раньше
        size = parseInt(element.getAttribute('data-size')) || 9;
        page = parseInt(element.getAttribute('data-page')) || 0;
    }

    const style = document.querySelector('.gallery-controls ul li.active')?.textContent || 'Вся галерея';
    const url = element.getAttribute('data-url');
    const container = element.getAttribute('data-container');

    const requestUrl = `${url}/${encodeURIComponent(style.trim())}/${page}/${size}`;

    console.log('Request URL:', requestUrl);
    console.log('Size from:', element.tagName === 'SELECT' ? 'select value' : 'data-size', size);

    const categoryInput = document.getElementById('category');
    if (categoryInput) {
        categoryInput.value = style.trim(); // Исправлено: style.trim() а не style.trim
    }

    $.get(requestUrl, {}, function(data) {
        $(container).html(data);
    });
}
    function goToPageSketches(element) {
         let size, page;

           // Проверяем, является ли элемент селектом (select)
           if (element.tagName === 'SELECT') {
               // Берем значение из выбранной опции
               size = parseInt(element.value) || 9;
               // Берем page из data-атрибута селекта
               page = parseInt(element.getAttribute('data-page')) || 0;
           } else {
               // Если это не селект, берем из атрибутов как раньше
               size = parseInt(element.getAttribute('data-size')) || 9;
               page = parseInt(element.getAttribute('data-page')) || 0;
           }


           const url = element.getAttribute('data-url');
           const container = element.getAttribute('data-container');

           const requestUrl = `${url}/${page}/${size}`;

           console.log('Request URL:', requestUrl);
           console.log('Size from:', element.tagName === 'SELECT' ? 'select value' : 'data-size', size);


           $.get(requestUrl, {}, function(data) {
               $(container).html(data);
           });
    }


    function goToPageGalleryReviews(page, number) {
        $.get(`/gallery/reviews/${page}/${number}`, {}, function(data) {
            $(".modal-img").html(data);
        });
    }


       /*------------------
            Функции для работы с модальным окном и отзывами
       --------------------*/
    function modals() {
        $('#myModal').modal("show");
        $('.btn-close').on('click', function() {
            $('#myModal').modal('hide');
        });
        $('.btn-secondary').on('click', function() {
            $('#myModal').modal('hide');
        });
        $('.btn-primary').attr('disabled', true);
    }


   // Показать модальное окно с галереей
   function showModals() {
       console.log('showModals вызвана');
       $('#infoModal').modal('show');
   }

   // Закрыть модальное окно
   function closeModal() {
       console.log('closeModal вызвана');
       $('#infoModal').modal('hide');
   }

// Функция для выбора изображения из модального окна (для отзывов)
function selectReviewImage(imageName) {
    console.log('selectReviewImage вызвана с imageName:', imageName);

    const reviewImage = document.getElementById('reviewImage');
    const placeholder = document.getElementById('reviewPlaceholder');
    const container = document.getElementById('reviewPreviewContainer');
    const imageNameInput = document.getElementById('reviewImageName');

    if (reviewImage && placeholder && container) {
        // Устанавливаем новый src
        reviewImage.src = '/images/' + imageName;

        // Показываем изображение, скрываем плейсхолдер
        reviewImage.style.display = 'block';
        placeholder.style.display = 'none';
        container.classList.add('has-image');

        console.log('Изображение установлено:', reviewImage.src);
    } else {
        console.error('Элементы не найдены:', {
            reviewImage: !!reviewImage,
            placeholder: !!placeholder,
            container: !!container
        });
    }

    // Сохраняем имя изображения в скрытом поле
    if (imageNameInput) {
        imageNameInput.value = imageName;
        console.log('Сохранено имя:', imageName);
    }

    // Закрываем модальное окно
    $('#infoModal').modal('hide');
}

// Функция для загрузки изображения в админке
function handleAdminImageUpload(input) {
 console.log('handleAdminImageUpload вызвана');
    if (input.files && input.files[0]) {
        const file = input.files[0];

        // Проверка размера файла (10MB)
        if (file.size > 10 * 1024 * 1024) {
            alert('Файл слишком большой. Максимальный размер 10MB');
            input.value = ''; // Очищаем input
            return;
        }

        // Проверка типа файла
        const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
        if (!validTypes.includes(file.type)) {
            alert('Поддерживаются только JPG, PNG, GIF и WEBP форматы');
            input.value = ''; // Очищаем input
            return;
        }

        const reader = new FileReader();

        reader.onload = function(e) {
            const preview = document.getElementById('adminPreviewImage');
            const placeholder = document.getElementById('adminPlaceholder');
            const container = document.getElementById('adminPreviewContainer');

            if (preview && placeholder && container) {
                preview.src = e.target.result;
                preview.style.display = 'block';
                placeholder.style.display = 'none';
                container.classList.add('has-image');
            }
        }

        reader.readAsDataURL(file);
    }
}

// Функция для закрытия модального окна
function closeModal() {
    $('#infoModal').modal('hide');
}

// Сброс формы отзыва
function resetReviewForm() {
    const preview = document.getElementById('reviewImage');
    const container = preview.closest('.review-image-preview');
    const placeholder = document.getElementById('review-placeholder');
    const fileInput = document.getElementById('review-image-file');
    const imageNameInput = document.getElementById('reviewImageName');
    const textarea = document.getElementById('review-comment');

    if (preview) {
        preview.src = '/img/placeholder-image.jpg';
        preview.classList.remove('image-loaded');
    }

    // Показываем плейсхолдер обратно
    if (container) {
        container.classList.remove('has-image');
    }
    if (placeholder) {
        placeholder.classList.remove('hidden');
    }

    if (fileInput) {
        fileInput.value = '';
    }

    if (imageNameInput) {
        imageNameInput.value = '';
    }

    if (textarea) {
        textarea.value = '';
    }

    // Сбрасываем рейтинг на 5 звезд
    const ratingInputs = document.querySelectorAll('input[name="rating"]');
    if (ratingInputs.length > 0) {
        document.getElementById('star5').checked = true;
    }
}
   // Предпросмотр загруженного изображения
   function previewReviewImage(input) {
       console.log('previewReviewImage вызвана');


       const preview = document.getElementById('reviewImage');
       const placeholder = document.getElementById('reviewPlaceholder');
       const imageNameInput = document.getElementById('reviewImageName');
       const container = document.getElementById('reviewPreviewContainer');

       if (input.files && input.files[0]) {
           const file = input.files[0];

           // Проверка типа файла
           if (!file.type.match('image.*')) {
               alert('Пожалуйста, выберите файл изображения (JPEG, PNG, GIF)');
               input.value = '';
               return;
           }

           // Проверка размера файла (5MB)
           if (file.size > 5 * 1024 * 1024) {
               alert('Файл слишком большой. Максимальный размер: 5MB');
               input.value = '';
               return;
           }

           const reader = new FileReader();

           reader.onload = function(e) {

            if (preview && placeholder && container) {
                preview.src = e.target.result;
                preview.style.display = 'block';
                placeholder.style.display = 'none';
                container.classList.add('has-image');
            }

               console.log('Изображение загружено');
           };

           reader.readAsDataURL(file);
       }
   }
    /*---------------
        CheckboxChange
    --------------*/

   function toggleFeatured(checkbox) {
       const isChecked = checkbox.checked;
       const id = checkbox.getAttribute('data-id');

       console.log('Toggling featured:', { id: id, flag: isChecked });

       // Блокируем чекбокс на время запроса
       checkbox.disabled = true;

       $.ajax({
           url: '/admin/gallery/toggle-featured',
           type: 'POST',
           data: {
               id: id,
               flag: isChecked
           },
           success: function(html) {
               // Проверяем, есть ли ошибка в HTML
               if (html.includes('text-danger') || html.includes('error')) {
                   console.error('Error toggling featured');
                   // Возвращаем чекбокс в исходное состояние
                   checkbox.checked = !isChecked;
               } else {
                   console.log('Featured toggled successfully');
               }
                console.log(html);
               // Показываем модальное окно с полученным HTML
               modals();
               $('.modal-body').html(html);
           },
           error: function(xhr, status, error) {
               console.error('AJAX error:', error);
               checkbox.checked = !isChecked;

               // Создаём сообщение об ошибке
               const errorHtml = '<div class="container-fluid"><p class="text-danger">Ошибка при обновлении статуса: ' + error + '</p></div>';
               modals();
               $('.modal-body').html(errorHtml);
           },
           complete: function() {
               // Разблокируем чекбокс
               checkbox.disabled = false;
           }
       });
   }
