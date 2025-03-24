var page = 0;
var number = 9;
$(document).ready(function() {});

function pagesRight() {
    page = page + 1;
    console.log(page)
    galleryControls()
}

function pagesLeft() {
    page = page - 1;
    galleryControls()
}

function numbers() {
    number = $('#number').val();
    page = 0;
    galleryControls()
}

function galleryControls() {
    $.get('/gallery/reviews/' + page + '/' + number, {}, function(data) {
        $(".modal-img").html(data);
    });
}

function uploadFragment(form) {
    form.on("submit", function(e) {
        const formData = new FormData(document.getElementById(form.attr('id')));
        e.preventDefault();
        const file = formData.get('file')
        if (file.size > 1048576) { // Ограничение размера файла до 1МБ: 1024 * 1024
            modals('Размер файла превышен, выберите файл меньше 1МБ.');
        } else {
            const xhr = new XMLHttpRequest();
            xhr.open('POST', form.attr('action'));
            xhr.send(formData);
            xhr.onload = () => {
                if (xhr.status == 200) {
                    $(".fragment-reviews").html(xhr.response);
                    document.getElementById('reviews').reset();
                    document.getElementById('imageID').reset();
                } else {
                    modals("Server response: ", xhr.response);
                }
            };
        }
    });
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