
$(document).ready(function() {
  $('#reviews').on("submit", function(e) {
          e.preventDefault();
          const fileInput = document.getElementById('file-reviews');
          const file = fileInput.files[0];
          if (file.size > 1048576) { // Ограничение размера файла до 1МБ: 1024 * 1024
              alert('Размер файла превышен, выберите файл меньше 1МБ.');
          } else {
              const xhr = new XMLHttpRequest();
              const formData = new FormData();
              formData.append('file', file);
              formData.append('name', $('#name').val())
              formData.append('Comment', $('#Comment').val())
              xhr.open('POST', '/reviews-import');
              xhr.send(formData);
              xhr.onload = () => {
                  if (xhr.status == 200) {
                      $(".fragment-reviews").html(xhr.response);
                      $('.show-result-select').niceSelect();
                      document.getElementById('reviews').reset();
                  } else {
                      alert("Server response: ", xhr.response);
                  }
              };
          }
      });
});
