
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

function updateAvatar(input, targetImage) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            targetImage.src = e.target.result;
            const avatar = document.getElementById('img-avatar');
            avatar.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
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