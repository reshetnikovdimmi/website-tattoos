$(document).ready(function() {

       sketches()
       reviews()



});

function reviews() {
 $(document).find('.del').on('click', function() {
            var id = $(this).parents('.row:first').find('.col:eq(0)').text(),
                data;
            console.log(id)
            $.get('/reviews-delete/' + id, {}, function(data) {
                $(".reviews").html(data);
                $('.show-result-select').niceSelect();
                importImg()
            });

        });
}
function importImg() {

     $(document).find('.delimg').on('click', function() {
            var id = $(this).parents('.row:first').find('.col:eq(0)').text(),
                data;
            console.log(id)
            $.get('/img-delete/' + id, {}, function(data) {
                $(".img-import").html(data);
                $('.show-result-select').niceSelect();
                importImg()
            });

        });
}
function sketches() {
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
                    sketches()
                } else {
                    alert("Server response: ", xhr.response);
                }
            };
        }
    });
    $(document).find('.del').on('click', function() {

        var id = $(this).parents('.row:first').find('.col:eq(0)').text(),
            data;
        console.log(id)
        $.get('/sketches-delete/' + id, {}, function(data) {
            $(".sketches-import").html(data);
            $('.show-result-select').niceSelect();
            sketches()
        });

    });

}