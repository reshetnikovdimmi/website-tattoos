/* ============================================================
   IMAGE MODULE - Загрузка изображений и отзывы
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * Загрузка изображения
     */
    function loadImage(input, targetImage) {
        if (input.files && input.files[0]) {
            const file = input.files[0];

            if (!file.type.match('image.*')) {
                showModalMessage('❌ Пожалуйста, выберите изображение', 'error');
                input.value = '';
                return;
            }

            if (file.size > 5 * 1024 * 1024) {
                showModalMessage('❌ Размер файла не должен превышать 5MB', 'error');
                input.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = function(e) {
                targetImage.src = e.target.result;
                const fileInfo = document.getElementById('file-info-logo');
                const fileNameSpan = document.querySelector('.upload-file-name');
                if (fileInfo && fileNameSpan) {
                    fileInfo.style.display = 'flex';
                    fileNameSpan.textContent = file.name;
                }
            };
            reader.readAsDataURL(file);
        } else {
            targetImage.src = targetImage.getAttribute('data-default-src') || '/img/default-logo.png';
            const fileInfo = document.getElementById('file-info-logo');
            if (fileInfo) fileInfo.style.display = 'none';
        }
    }

    /**
     * Обновить логотип
     */
    function updateLogoImage(input, targetImage) {
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                targetImage.src = e.target.result;
                document.getElementById('logo-header').src = e.target.result;
                document.getElementById('logo-footer').src = e.target.result;
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    /**
     * Обновить фон хлебных крошек
     */
    function updateBreadcrumbBackground(input, targetImage) {
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                targetImage.src = e.target.result;
                document.getElementById('breadcrumb-section').style.backgroundImage = `url('${e.target.result}')`;
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    /**
     * Предпросмотр изображения отзыва
     */
    function previewReviewImage(input) {
        if (input.files && input.files[0]) {
            const file = input.files[0];
            if (!file.type.match('image.*')) {
                alert('Пожалуйста, выберите файл изображения (JPEG, PNG, GIF)');
                input.value = '';
                return;
            }
            if (file.size > 5 * 1024 * 1024) {
                alert('Файл слишком большой. Максимальный размер: 5MB');
                input.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = function(e) {
                const preview = document.getElementById('reviewImage');
                const placeholder = document.getElementById('reviewPlaceholder');
                const container = document.getElementById('reviewPreviewContainer');
                if (preview && placeholder && container) {
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                    placeholder.style.display = 'none';
                    container.classList.add('has-image');
                }
            };
            reader.readAsDataURL(file);
        }
    }

    /**
     * Выбрать изображение отзыва
     */
    function selectReviewImage(imageName) {
        const reviewImage = document.getElementById('reviewImage');
        const placeholder = document.getElementById('reviewPlaceholder');
        const imageNameInput = document.getElementById('reviewImageName');

        if (reviewImage && placeholder) {
            reviewImage.src = '/images/' + imageName;
            reviewImage.style.display = 'block';
            placeholder.style.display = 'none';
        }

        if (imageNameInput) imageNameInput.value = imageName;
        $('#infoModal').modal('hide');
    }

    /**
     * Сбросить форму отзыва
     */
    function resetReviewForm() {
        const preview = document.getElementById('reviewImage');
        const container = preview?.closest('.review-image-preview');
        const placeholder = document.getElementById('review-placeholder');
        const fileInput = document.getElementById('review-image-file');
        const imageNameInput = document.getElementById('reviewImageName');
        const textarea = document.getElementById('review-comment');

        if (preview) {
            preview.src = '/img/placeholder-image.jpg';
            preview.classList.remove('image-loaded');
        }
        if (container) container.classList.remove('has-image');
        if (placeholder) placeholder.classList.remove('hidden');
        if (fileInput) fileInput.value = '';
        if (imageNameInput) imageNameInput.value = '';
        if (textarea) textarea.value = '';

        const ratingInputs = document.querySelectorAll('input[name="rating"]');
        if (ratingInputs.length > 0) {
            document.getElementById('star5').checked = true;
        }
    }

    /**
     * Переключить featured
     */
    function toggleFeatured(checkbox) {
        const isChecked = checkbox.checked;
        const id = checkbox.getAttribute('data-id');

        checkbox.disabled = true;
        
        // H1 FIX: CSRF-токен для POST-запросов
        const csrfToken = $('meta[name="_csrf"]').attr('content');
        
        $.ajax({
            url: '/admin/gallery/toggle-featured',
            type: 'POST',
            data: { id: id, flag: isChecked },
            headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {},
            success: function(html) {
                modals();
                $('.modal-body').html(html);
            },
            error: function(xhr) {
                checkbox.checked = !isChecked;
                modals();
                $('.modal-body').html(xhr.responseText);
            },
            complete: function() {
                checkbox.disabled = false;
            }
        });
    }

    /**
     * Отправить email
     * H3 FIX: убран $("#preloder").delay() из catch
     */
    async function sendMail(event, fragmentPrefix) {
        event.preventDefault();
        const form = event.currentTarget;
        const formAction = form.action;
        const formData = new FormData(form);

        const nameInput = form.querySelector('[name=name]');
        const subjectInput = form.querySelector('[name=subject]');
        const messageTextarea = form.querySelector('[name=msgBody]');
        const phoneRegex = /^(\+7|8)?\d{10}$/;
        let errors = [];

        // Honeypot spam protection
        const honeypot = form.querySelector('[name=website-url]');
        if (honeypot && honeypot.value.trim()) {
            console.log('⚠️ Honeypot: обнаружен бот, форма отклонена');
            return; // Блокировать отправку от бота
        }

        if (!nameInput.value.trim()) {
            errors.push('Имя не заполнено');
        } else if (nameInput.value.length < 2) {
            errors.push('Имя должно содержать минимум 2 символа');
        }

        if (!subjectInput.value.trim()) {
            errors.push('Телефон не указан');
        } else if (!phoneRegex.test(subjectInput.value)) {
            errors.push('Неправильный формат телефона');
        }

        if (!messageTextarea.value.trim()) {
            errors.push('Сообщение не заполнено');
        } else if (messageTextarea.value.length > 500) {
            errors.push('Длина сообщения превышает допустимый предел');
        }

        if (errors.length > 0) {
            alert(errors.join('\n'));
            return;
        }

        try {
            const response = await $.ajax({
                url: formAction,
                dataType: 'html',
                cache: false,
                contentType: false,
                processData: false,
                data: formData,
                type: 'POST',
            });

            $(fragmentPrefix).html(response);
            const statusMessageElement = $('.status-message');
            statusMessageElement.show();
            setTimeout(() => statusMessageElement.hide(), 15000);

        } catch (error) {
            // H3 FIX: убрана бессмысленная строка с preloder
            alert('Произошла ошибка при сохранении информации.');
        }
    }

    // Экспорт в глобальную область
    window.ImageModule = {
        loadImage: loadImage,
        updateLogoImage: updateLogoImage,
        updateBreadcrumbBackground: updateBreadcrumbBackground,
        previewReviewImage: previewReviewImage,
        selectReviewImage: selectReviewImage,
        resetReviewForm: resetReviewForm,
        toggleFeatured: toggleFeatured,
        sendMail: sendMail
    };

    // Глобальные функции для HTML onclick/onchange
    window.loadImage = loadImage;
    window.updateLogoImage = updateLogoImage;
    window.updateBreadcrumbBackground = updateBreadcrumbBackground;
    window.previewReviewImage = previewReviewImage;
    window.selectReviewImage = selectReviewImage;
    window.resetReviewForm = resetReviewForm;
    window.toggleFeatured = toggleFeatured;
    window.sendMail = sendMail;
})(window);

    /**
     * Делегированный обработчик для форм с data-form
     */
    function initFormDelegation() {
        document.addEventListener('submit', function(event) {
            var form = event.currentTarget;
            if (form.hasAttribute('data-form')) {
                sendMail(event);
            }
        });
    }

    // Инициализация делегирования при DOM ready
    $(document).ready(function() {
        initFormDelegation();
    });
