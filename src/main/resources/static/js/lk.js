 function showModals() {

              $('#infoModal').modal("show");
          }

          function hideModal(modalId) {
              document.getElementById('infoModal').style.display = 'none';
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

function addFragmet(dfeg){
console.log(dfeg)
}