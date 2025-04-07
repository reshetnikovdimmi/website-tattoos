
   var style = "Вся галерея"; // Стартовый стиль
    var page = 0; // Стартовая страница
    var number = 9; // Количество изображений на странице
$(document).ready(function() {
$('.gallery-controls ul li').on('click', function() {
        page = 0;
        style = $(this).text();
        galleryControls() // Обновляем галерею
        return false;
    });
});
    /**
     * Управляет переходом на следующую или предыдущую страницу.
     * @param {string} direction 'right' для следующей страницы, 'left' для предыдущей.
     */
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
            $.get(`/gallery/${style}/${page}/${number}`, {}, function(data) {
                $(".galleryFilter").html(data);
            });
        }