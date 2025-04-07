var page = 0;
var number = 9;
  function navigatePages(direction) {
        switch (direction) {
            case 'right':
                page += 1;
                break;
            case 'left':
                page -= 1;
                break;
            case 'number': // Когда меняется количество изображений на странице
                number = parseInt($('#number').val()) || 9; // Если значение некорректное, устанавливаем число по умолчанию (9)
                page = 0; // Сбрасываем номер страницы
                break;

        }
        galleryControls(); // Обновляем галерею
    }

function galleryControls() {
    $.get('/sketches/' + page + '/' + number, {}, function(data) {
        $(".galleryFilter").html(data);
    });
}