var page = 0;
var number = 9;
$(document).ready(function() {
    pagesRight()
    pagesLeft()
    numbers()
    var target = document.querySelector('.galleryFilter');
    MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
    var observer = new MutationObserver(function(mutations, observer) {
        pagesRight()
        pagesLeft()
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
        page = page - 1;
        galleryControls()
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
    $.get('/sketches/' + page + '/' + number, {}, function(data) {
        $(".galleryFilter").html(data);
    });
}