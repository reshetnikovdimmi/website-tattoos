function uploadHome(event) {
    event.preventDefault(); // Отмена стандартной отправки формы
    const form = event.target; // получаем форму через событие
    const formAction = form.action; // Извлекаем адрес из атрибута action
    const formData = new FormData(form); // Создаем объект FormData с полями формы
    $.ajax({
        url: formAction, // Адрес контроллера Spring MVC
        dataType: 'html', // Тип ожидаемого ответа — HTML-фрагмент
        cache: false,
        contentType: false,
        processData: false,
        data: formData,
        type: 'POST',
        success: function(data) {
            alert('Татуировка успешно сохранена!');
            console.log('Татуировка успешно сохранена!');
            $('.'+formAction.split('/')[5]).html(data); // Замена текущего содержимого новым шаблоном
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log('Ошибка при загрузке:', errorThrown);
            alert('Произошла ошибка при сохранении татуировки.');
        }
    });
}

function loadImage(input, targetImage) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            targetImage.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function deleteImg() {
    $(document).find('.carousel-import button').on('click', function() {
        var id = this.id;
        $.get('/admin/carousel-delete/' + id, {}, function(data) {
            $(".carousel-import").html(data);
        });
    });
}