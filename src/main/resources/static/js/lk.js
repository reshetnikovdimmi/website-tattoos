 var page = 0;
 var number = 9;
 $(document).ready(function() {});

 function pagesRight() {
     page = page + 1;
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
     $.get('/sketchesrs/' + page + '/' + number, {}, function(data) {
         $(".modal-img").html(data);
     });
 }

 function showModals() {
     $('#infoModal').modal("show");
 }

 function hideModal(modalId) {

$('#imageID').attr('src', '/images/'+ modalId);
$('#imageName').val(modalId);
// document.getElementById('imageID').src = modalId;
     $('#infoModal').modal("hide");
 }

 function closeModal() {
     $('#infoModal').modal("hide");
 }

 function loadImage(input) {
     if (input.files && input.files[0]) {
         var reader = new FileReader();
         reader.onload = function(e) {
             document.getElementById('previewimage').src = e.target.result;
         };
         reader.readAsDataURL(input.files[0]);
     }
 }

 function uploadFragment(form, fragment) {
     form.on("submit", function(e) {
         const formData = new FormData(document.getElementById(form.attr('id')));
         e.preventDefault();
         const file = formData.get('file')
         console.log(file)
         if (file.size > 1048576) { // Ограничение размера файла до 1МБ: 1024 * 1024
             modals('Размер файла превышен, выберите файл меньше 1МБ.');
         } else {
             const xhr = new XMLHttpRequest();
             xhr.open('POST', form.attr('action'));
             xhr.send(formData);
             xhr.onload = () => {
                 if (xhr.status == 200) {
                     $(fragment).html(xhr.response);
                     // document.getElementById('img-interesting-works').reset();
                 } else {
                     modals("Server response: ", xhr.response);
                 }
             };
         }
     });
 }
     /*------------------
          User info
     --------------------*/
     $('#info').click(function(e) {
         $.get('/user-info', {}, function(data) {
             $(".container-lk-info").html(data);

         });
     });
     $('#profile-editing').click(function(e) {
         $.get('/profile-editing', {}, function(data) {
             $(".container-lk-info").html(data);

         });
     });
     $('#user-tattoos').click(function(e) {
         $.get('/user-tattoos', {}, function(data) {
             $(".container-lk-info").html(data);

         });
     });