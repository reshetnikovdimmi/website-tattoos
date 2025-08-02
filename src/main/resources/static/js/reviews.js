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

function uploadFragment(event) {
console.log("ok");
           event.preventDefault(); // Отмена стандартной отправки формы
           const form = event.currentTarget; // Получаем текущую форму
           const formAction = form.action; // Извлекаем адрес из атрибута action
           const formData = new FormData(form); // Создаем объект FormData с полями формы
           const image = document.getElementById('imageID'); // Получаем элемент изображения
           const fileName = image.src.split('/').pop();// Получаем имя файла из атрибута src
            formData.append('imageName', fileName);
            console.log(formAction)
            const xhr = new XMLHttpRequest();
            xhr.open('POST', formAction);
            xhr.send(formData);
            xhr.onload = () => {
                if (xhr.status == 200) {
                    $(".fragment-reviews").html(xhr.response);
                   } else {
                    modals("Server response: ", xhr.response);
                }
            };


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