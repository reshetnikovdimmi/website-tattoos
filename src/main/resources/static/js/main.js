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
        $('#myTab button').click(function(e) {
            e.preventDefault();
            (this).tab('show');
        });
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
    $('.show-result-select').niceSelect();
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
    /*------------------
         Carousel-admin
    --------------------*/
    function carouselNext() {
        $('#carouselExample').carousel('next');
    }

    function carousePrevious() {
        $('#carouselExample').carousel('prev');
    }
    /*----------------------------
    Функция для загрузки данных формы
    -----------------------------*/
    async function submitForm(event, fragment) {

        event.preventDefault(); // Отмена стандартной отправки формы
        const form = event.currentTarget; // Получаем текущую форму
        const formAction = form.action; // Извлекаем адрес из атрибута action
        console.log(formAction);
        if (!form) {
            console.error('Форма не найдена');
            return;
        }
        const formData = new FormData(form); // Создаем объект FormData с полями формы
        // Добавляем id в FormData, если он передан
        if (event.target.id) {
            formData.append('id', event.currentTarget.id);
        }

        const inputs = form.querySelectorAll('input, textarea');
        const button = form.querySelector('button[type="submit"]');
        if (button.textContent.trim().toLowerCase() === 'изменить') {
            // Включаем поля для редактирования
            inputs.forEach(input => input.disabled = false);
            button.textContent = 'Сохранить'; // Меняем надпись на кнопке
            form.classList.add('active'); // Добавляем класс 'active' к форме
        } else {
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
                alert('Информация успешно сохранена!');
                console.log('Информация успешно сохранена!');
                $(fragment).html(response); // Замена текущего содержимого новым шаблоном
                // Меняем название кнопки на "Изменить"
                button.textContent = 'Изменить';
                // Делаем поля ввода неактивными
                inputs.forEach(input => input.disabled = true);
                // Удаляем класс 'active' у формы
                form.classList.remove('active');
            } catch (error) {
                console.log('Ошибка при загрузке:', error);
                alert('Произошла ошибка при сохранении информации.');
            }
        }
    }
    /*---------------
    Gallery controls
    --------------*/
    function goToPageGalleryAdmin(style, page, number) {
        $.get(`/gallery/admin/${style.trim()}/${page}/${number}`, {}, function(data) {
            document.getElementById('category').value = style;
            $(".img-import").html(data);
        });
    }
    function goToPageGallery(style, page, number) {
        $.get(`/gallery/${style.trim()}/${page}/${number}`, {}, function(data) {
            $(".galleryFilter").html(data);
        });
    }
    function goToPageSketches(page, number) {
        $.get(`/sketches/${page}/${number}`, {}, function(data) {
            $(".galleryFilter").html(data);
        });
    }
    function goToPageSketchesAdmin(page, number) {
        $.get(`/sketches/admin/${page}/${number}`, {}, function(data) {
            $(".sketches-import").html(data);
        });
    }

    function goToPageGalleryReviews(page, number) {
        $.get(`/gallery/reviews/${page}/${number}`, {}, function(data) {
            $(".modal-img").html(data);
        });
    }

    /*---------------
        Modals controls
     --------------*/
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
    function showModals() {
        $('#infoModal').modal("show");
    }

    function hideModal(modalId) {
        $('#imageID').attr('src', '/images/' + modalId);
        $('#imageName').val(modalId);
        // document.getElementById('imageID').src = modalId;
        $('#infoModal').modal("hide");
    }

    function closeModal() {
        $('#infoModal').modal("hide");
    }
    /*---------------
        CheckboxChange
    --------------*/
    function handleCheckboxChange(checkbox) {
        const form = $(checkbox).closest('form');
        const isChecked = $(checkbox).is(':checked');
        const id = form.attr('id');

        $.ajax({
            url: '/gallery/admin/update-flag',
            type: 'POST',
            data: {
                id: id,
                flag: isChecked
            },
            success: function(response) {
                console.log('Флаг обновлён:');
                modals();
                $('.modal-body').html(response);
            },
            error: function(error) {
                console.log('Ошибка при обновлении флага:', error);
                modals();
                $('.modal-body').html(response);
            }
        });
    }