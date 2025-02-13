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
        document.getElementById('file-carousel').onchange = function(event) {
        var reader = new FileReader();
        reader.onload = function() {
            var output = document.getElementById('preview');
            output.src = reader.result;
            output.style.display = 'block';
            output.style.width = '200px';
            output.style.height = '200px';

            output.style.top = '50%';
            output.style.left = '50%';

        };
        reader.readAsDataURL(event.target.files[0]);

    };

    $('#img-import').on("submit", function(e) {
     alert("file")
            e.preventDefault();
            const fileInput = document.getElementById('file-Gallery');
            const file = fileInput.files[0];
            if (file.size > 1048576) { // Ограничение размера файла до 1МБ: 1024 * 1024
                alert('Размер файла превышен, выберите файл меньше 1МБ.');
            } else {
            alert(file)
                const xhr = new XMLHttpRequest();
                const formData = new FormData();
                formData.append('file', file);
                formData.append('category', $('#select').val())
                formData.append('description', $('#description').val())
                xhr.open('POST', '/img-import');
                xhr.send(formData);
                xhr.onload = () => {
                    if (xhr.status == 200) {
                        $(".img-import").html(xhr.response);
                        $('.show-result-select').niceSelect();
                        importImg()
                    } else {
                        alert("Server response: ", xhr.response);
                    }
                };
            }
        });

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
            xhr.open('POST', '/interesting-works-import');
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
            $.get('/interesting-works-delete/' + id, {}, function(data) {
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
            Home-Carousel
    --------------------*/

    $('#img-carousel').on("submit", function(e) {
        e.preventDefault();
        const fileInput = document.getElementById('file-carousel');
        const file = fileInput.files[0];
        if (file.size > 1048576) { // Ограничение размера файла до 1МБ: 1024 * 1024
            modals('Размер файла превышен, выберите файл меньше 1МБ.');
        } else {
            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('file', file);
            xhr.open('POST', '/carousel-import');
            console.log(file.name)
            xhr.send(formData);
            xhr.onload = () => {
                if (xhr.status == 200) {
                    $(".carousel-import").html(xhr.response);
                    $('.show-result-select').niceSelect();
                    document.getElementById('img-carousel').reset();
                    deleteImg()
                } else {
                    modals("Server response: ", xhr.response);
                }
            };
        }
    });
    deleteImg()

    function deleteImg() {
        $(document).find('.carousel-import button').on('click', function() {

            var id = this.id;

            $.get('/carousel-delete/' + id, {}, function(data) {
                $(".carousel-import").html(data);
                $('.show-result-select').niceSelect();
                deleteImg()
            });
        });
    }
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
        Gallery admin
    --------------------*/
    $(document).find('.checkbox').on('click', function() {
        var id = $(this).parents('.row:first').find('.col:eq(0)').text(),
            data;
        const body = {};
        body.id = $(this).parents('.row:first').find('.col:eq(0)').text(), data;
        body.flag = this.checked;
        sendRequest('POST', '/best-tattoos', body).then(data => modals(data)).catch(err => modals(err))
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