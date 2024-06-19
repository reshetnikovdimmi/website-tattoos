var style = "Вся галерея";
var page = 0;
var number = 9;
$(document).ready(function() {
        pagesRight()
        pagesLeft()
        styles()
        numbers()
 var target = document.querySelector('.galleryFilter');

    MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
    var observer = new MutationObserver(function(mutations, observer) {
        pagesRight()
        pagesLeft()
        styles()
        numbers()

    });
    var config = {
        attributes: true,
        childList: true,
        characterData: true
    }
    observer.observe(target, config);


});
function pagesRight() {
 $('#fa-long-arrow-right').on('click', function() {
       page = page + 1;
        galleryControls()
    });
}
function pagesLeft() {
 $('#fa-long-arrow-left').on('click', function() {
       page = page -1;
        galleryControls()
    });
}
function styles() {
 $('.gallery-controls ul li').on('click', function() {
        page = 0;
        style = $(this).text();
        galleryControls()
        return false;
    });
}
function numbers() {
 $('#number').on('change', function() {
              number = $('#number').val();
              page = 0;
              galleryControls()
                });
}
function galleryControls() {

    $.get('/gallery' + '/' + style + '/' + page + '/' + number, {}, function(data) {

        $(".galleryFilter").html(data);

$('.show-result-select').niceSelect();
    });
}
