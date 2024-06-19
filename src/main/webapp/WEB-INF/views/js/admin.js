$(document).ready(function() {
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
            formData.append('category', $('#select').val())
            formData.append('description', $('#description').val())
            xhr.open('POST', '/img-import');
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
            xhr.open('POST', '/sketches-import');
            xhr.send(formData);
            xhr.onload = () => {
                if (xhr.status == 200) {
                    $(".sketches-import").html(xhr.response);
                    $('.show-result-select').niceSelect();
                } else {
                    alert("Server response: ", xhr.response);
                }
            };
        }
    });
});