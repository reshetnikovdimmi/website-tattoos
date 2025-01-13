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

$('#myTab button').click(function(e){
console.log(e)
    e.preventDefault();
    $(this).tab('show');
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
                        var id = $(this).parents('.row:first').find('.col:eq(0)').text(),
                            data;
                            console.log(id)
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

