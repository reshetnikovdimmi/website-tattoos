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

        galleryAdmin()
        reviews()
        sketches()
        var target = document.querySelector('.img-import');
        var target1 = document.querySelector('.sketches-import');
        MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
        var observer = new MutationObserver(function(mutations, observer) {
            galleryAdmin()
            reviews()
            $('.show-result-select').niceSelect();
        });
        var observer1 = new MutationObserver(function(mutations, observer1) {
            sketches()
            $('.show-result-select').niceSelect();
        });
        var config = {
            attributes: true,
            childList: true,
            characterData: true
        }
        observer.observe(target, config);
        observer1.observe(target1, config);
    });
    /*------------------
            carousel
       --------------------*/
    $('#carouselExample').carousel({
        interval: 10000,
        keyboard: false,
        pause: 'hover',
        ride: 'carousel',
        wrap: false
    });
    $('#Next').click(function(e) {
        $('#carouselExample').carousel('next');
    });
    $('#Previous').click(function(e) {
        $('#carouselExample').carousel('prev');
    });
    /*------------------
         User info
    --------------------*/
    $('#info').click(function(e) {
        $.get('/user-info', {}, function(data) {
            $(".container-lk-info").html(data);

        });
    });
    $('#profile-editing').click(function(e) {
        $.get('/profile-editing', {}, function(data) {
            $(".container-lk-info").html(data);

        });
    });
    $('#user-tattoos').click(function(e) {
        $.get('/user-tattoos', {}, function(data) {
            $(".container-lk-info").html(data);

        });
    });




    function userUpdate(inputFile, link, fragment) {

        const fileInput = document.getElementById(inputFile);
        const file = fileInput.files[0];

        if (file.size > 1048576) { // Ограничение размера файла до 1МБ: 1024 * 1024
            modals('Размер файла превышен, выберите файл меньше 1МБ.');
        } else {

            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('file', file);
            xhr.open('POST', link);

            if (typeof $('#Comment').val() != "undefined") {
            formData.append('Comment', $('#Comment').val())
            }
            xhr.send(formData);
            xhr.onload = () => {
                if (xhr.status == 200) {
                    $(fragment).html(xhr.response);

                    // document.getElementById('img-interesting-works').reset();
                } else {
                    modals("Server response: ", xhr.response);
                }
            };
        }
    }
    /*------------------
       Interesting works
    --------------------*/
    $('#img-interesting-works').on("submit", function(e) {
        e.preventDefault();
        const fileInput = document.getElementById('file-interesting-works');
        const file = fileInput.files[0];
        if (file.size > 1048576) { // Ограничение размера файла до 1МБ: 1024 * 1024
            modals('Размер файла превышен, выберите файл меньше 1МБ.');
        } else {
            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('file', file);
            formData.append('description', $('#description-interesting-works').val())
            xhr.open('POST', '/admin/interesting-works-import');
            xhr.send(formData);
            xhr.onload = () => {
                if (xhr.status == 200) {
                    $(".interesting-works-import").html(xhr.response);
                    $('.show-result-select').niceSelect();
                    document.getElementById('img-interesting-works').reset();
                    del()
                } else {
                    modals("Server response: ", xhr.response);
                }
            };
        }
    });
    del()

    function del() {
        $(document).find('.interesting-works-del').on('click', function() {
            var id = $(this).parents('.row:first').find('.col:eq(0)').text(),
                data;
            $.get('/admin/interesting-works-delete/' + id, {}, function(data) {
                $(".interesting-works-import").html(data);
                $('.show-result-select').niceSelect();
                del()
            });
        });
    }
    /*------------------
            Home
    --------------------*/
    $('#myTab button').click(function(e) {
        e.preventDefault();
        $(this).tab('show');
    });
      $('#tabOption button').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    /*------------------
      Contact info
     --------------------*/
    window.addEventListener("DOMContentLoaded", function() {
        var input = document.querySelector("#tell");
        input.addEventListener("input", mask, false);
        input.focus();
        setCursorPosition(3, input);
    });
    const body = {
        tell: null,
        email: null,
        address: null
    };
    $('#phone').on('click', function() {
        var regex = /^\+?\d{1,4}?[-.\s]?\(?\d{1,3}?\)?[-.\s]?\d{1,4}[-.\s]?\d{1,4}[-.\s]?\d{1,9}$/;
        if (regex.test($('#tell').val())) {
            body.tell = $('#tell').val();
        } else {
            modals("Введите номер телефона");
        }
        sendRequest('POST', '/contact-info', body).then(data => {
            $($("#tell-new").contents()[2]).replaceWith(data.tell);
            $($("#tell-footer").contents()[4]).replaceWith(data.tell);
            $('#tell').val('');
        }).catch(err => modals(err))
    });
    $('#email-btn').on('click', function() {
        const emailField = document.getElementById('email');
        if (emailField.checkValidity()) {
            body.email = $('#email').val();
        } else {
            modals('Please enter a valid email address.');
        }
        sendRequest('POST', '/contact-info', body).then(data => {
            $($("#email-new").contents()[2]).replaceWith(data.email);
            $($("#email-footer").contents()[4]).replaceWith(data.email);
            $('#email').val('');
        }).catch(err => modals(err))
    });
    $('#address-btn').on('click', function() {
        body.address = $('#address').val();
        sendRequest('POST', '/contact-info', body).then(data => {
            $($("#address-new").contents()[2]).replaceWith(data.address);
            $($("#address-footer").contents()[4]).replaceWith(data.address);
            $('#address').val('');
        }).catch(err => modals(err))
    });

    /*------------------
        Comments
    --------------------*/
    $('#commit').on("submit", function(e) {
        e.preventDefault();
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('Comment', $('#Comment').val())
        formData.append('name', $('#name').text())
        xhr.open('POST', '/commits-import');
        xhr.send(formData);
        xhr.onload = () => {
            if (xhr.status == 200) {
                $(".fragment-commits").html(xhr.response);
                document.getElementById('commit').reset();
                modals("Сохранено")
            } else {
                modals("Server response: ", xhr.response);
            }
        };
    });
    /*------------------
        Admin sketchers
        ------------------*/
    $('#img-sketches').on("submit", function(e) {
        e.preventDefault();
        const fileInput = document.getElementById('file-sketches');
        const file = fileInput.files[0];
        if (file.size > 1048576) { // Ограничение размера файла до 1МБ: 1024 * 1024
            alert('Размер файла превышен, выберите файл меньше 1МБ.');
        } else {
            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('file', file);
            formData.append('description', $('#description-sketches').val())
            xhr.open('POST', '/admin/sketches-import');
            xhr.send(formData);
            xhr.onload = () => {
                if (xhr.status == 200) {
                    $(".sketches-import").html(xhr.response);
                    $('.show-result-select').niceSelect();
                    sketches()
                } else {
                    alert("Server response: ", xhr.response);
                }
            };
        }
    });
    var s_page = 0;
    var s_number = 9;

    function sketches() {
        $('#sketches-number').on('change', function() {
            s_number = $('#sketches-number').val();
            s_page = 0;
            sketchesControls()
        });
        $('#sketchers-left').on('click', function() {
            s_page = s_page - 1;
            sketchesControls()
        });
        $('#sketchers-right').on('click', function() {
            s_page = s_page + 1;
            sketchesControls()
        });
        $('.sketches-import button').click(function(e) {
            var id = $(this).attr("id");
            $.get('/admin/sketches-delete/' + id, {}, function(data) {
                $(".sketches-import").html(data);
            });
        });
    }

    function sketchesControls() {
        $.get('/admin/admin-sketches/' + s_page + '/' + s_number, {}, function(data) {
            $(".sketches-import").html(data);
        });
    }
    /*------------------
    Admin reviews
    ------------------*/
    function reviews() {
        $(document).find('.del-reviews').on('click', function() {
            var id = $(this).attr("id");
            $.get('/admin/reviews-delete/' + id, {}, function(data) {
                $(".reviews").html(data);
            });
        });
    }
    /*------------------
        Gallery admin
    --------------------*/
    var style = "Вся галерея";
    var page = 0;
    var number = 9;

    function galleryControls() {
        $.get('/admin' + '/' + style + '/' + page + '/' + number, {}, function(data) {
            $(".img-import").html(data);
        });
    }
    $('#img-import').on("submit", function(e) {
        e.preventDefault();
        const fileInput = document.getElementById('file-Gallery');
        const file = fileInput.files[0];
        if (file.size > 1048576) { // Ограничение размера файла до 1МБ: 1024 * 1024
            alert('Размер файла превышен, выберите файл меньше 1МБ.');
        } else {
            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('file', file);
            formData.append('category', style)
            formData.append('description', $('#description').val())
            xhr.open('POST', '/admin/img-import');
            xhr.send(formData);
            xhr.onload = () => {
                if (xhr.status == 200) {
                    $(".img-import").html(xhr.response);
                    $('.show-result-select').niceSelect();
                } else {
                    alert("Server response: ", xhr.response);
                }
            };
        }
    });

    function galleryAdmin() {
        $('#number').on('change', function() {
            number = $('#number').val();
            page = 0;
            galleryControls()
        });
        $('#fa-long-arrow-left').on('click', function() {
            page = page - 1;
            galleryControls()
        });
        $('#fa-long-arrow-right').on('click', function() {
            page = page + 1;
            galleryControls()
        });
        $('.gallery-controls ul li').on('click', function() {
            page = 0;
            style = $(this).text();
            galleryControls()
            return false;
        });
        $(document).find('.checkbox').on('click', function() {
            const body = {};
            body.id = $(this).parents('.reviews-admin').attr('id');
            body.flag = this.checked;
            sendRequest('POST', '/best-tattoos', body).then(data => modals(data)).catch(err => modals(err))
        });
        $('.img-import button').click(function(e) {
            var id = $(this).parent().attr("id");
            $.get('/admin/img-delete/' + id, {}, function(data) {
                $(".img-import").html(data);
            });
        })
    }
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

function sendRequest(method, url, body = null) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest()
        xhr.open(method, url)
        xhr.responseType = 'json'
        xhr.setRequestHeader('Content-Type', 'application/json')
        xhr.onload = () => {
            if (xhr.status >= 400) {
                reject(xhr.response)
            } else {
                resolve(xhr.response)
            }
        }
        xhr.onerror = () => {
            reject(xhr.response)
        }
        xhr.send(JSON.stringify(body))
    })
}

function modals(message) {
    $('#myModal').modal("show");
    document.querySelector('.modal-body').textContent = message;
    $('.btn-close').on('click', function() {
        $('#myModal').modal('hide');
    });
    $('.btn-secondary').on('click', function() {
        $('#myModal').modal('hide');
    });
    $('.btn-primary').attr('disabled', true);
}

function setCursorPosition(pos, e) {
    e.focus();
    if (e.setSelectionRange) e.setSelectionRange(pos, pos);
    else if (e.createTextRange) {
        var range = e.createTextRange();
        range.collapse(true);
        range.moveEnd("character", pos);
        range.moveStart("character", pos);
        range.select()
    }
}

function mask(e) {
    //console.log('mask',e);
    var matrix = this.placeholder, // .defaultValue
        i = 0,
        def = matrix.replace(/\D/g, ""),
        val = this.value.replace(/\D/g, "");
    def.length >= val.length && (val = def);
    matrix = matrix.replace(/[_\d]/g, function(a) {
        return val.charAt(i++) || "_"
    });
    this.value = matrix;
    i = matrix.lastIndexOf(val.substr(-1));
    i < matrix.length && matrix != this.placeholder ? i++ : i = matrix.indexOf("_");
    setCursorPosition(i, this)
}