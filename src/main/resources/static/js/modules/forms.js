/* ============================================================
   FORMS MODULE - Формы и модалки
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * Получить информацию о фрагменте из формы
     */
    function getFragmentInfo(form) {
        // 1. data-атрибуты
        if (form.dataset.fragmentName && form.dataset.containerSelector) {
            return {
                fragmentName: form.dataset.fragmentName,
                containerSelector: form.dataset.containerSelector
            };
        }

        // 2. data-fragment с разделителем
        if (form.dataset.fragment && form.dataset.fragment.includes('|')) {
            const [fragmentName, containerSelector] = form.dataset.fragment.split('|');
            return { fragmentName, containerSelector };
        }

        // 3. Автоматический поиск
        let fragmentName = null;
        let containerSelector = null;
        let parent = form.parentElement;

        while (parent && parent !== document.body) {
            if (parent.hasAttribute('th:fragment')) {
                fragmentName = parent.getAttribute('th:fragment');
                const container = form.closest('[class*="-import"]');
                if (container) {
                    const classes = container.className.split(' ');
                    containerSelector = '.' + classes.find(c => c.includes('-import'));
                }
                break;
            }
            parent = parent.parentElement;
        }

        return { fragmentName, containerSelector };
    }

    /**
     * Отправка формы
     * C1 FIX: event.target.id вместо event.currentTarget.id
     */
    async function submitForm(event) {
        event.preventDefault();
        const form = event.currentTarget;
        const formAction = form.action;
        const button = form.querySelector('button[type="submit"]');
        const inputs = form.querySelectorAll('input, textarea');
        const originalText = button.textContent;

        if (!form) {
            console.error('Форма не найдена');
            return;
        }

        const { fragmentName, containerSelector } = getFragmentInfo(form);

        // Режим редактирования
        if (button.textContent.trim().toLowerCase() === 'изменить') {
            inputs.forEach(input => input.disabled = false);
            button.textContent = 'Сохранить';
            form.classList.add('active');
            $(button).html('<i class="fa fa-save"></i> ' + button.textContent);
            return;
        }

        const formData = new FormData(form);
        if (fragmentName) {
            formData.append('fragment', fragmentName);
        }

        // C1 FIX: используем event.target.id (кнопка), а не event.currentTarget.id (форма)
        if (event.target.id) {
            formData.append('id', event.target.id);
        }

        button.disabled = true;
        button.textContent = 'Сохранение...';

        try {
            // H1 FIX: CSRF-токен для POST-запросов форм
            const csrfToken = $('meta[name="_csrf"]').attr('content');
            
            const response = await $.ajax({
                url: formAction,
                dataType: 'html',
                cache: false,
                contentType: false,
                processData: false,
                data: formData,
                type: 'POST',
                headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {}
            });

            console.log('✅ Ответ получен');

            showModalMessage('✅ SUCCESS!');

            if (response && containerSelector && $(containerSelector).length) {
                $(containerSelector).html(response);
                reinitSetBg();
                reinitAfterFragmentUpdate(containerSelector);
            }

            button.textContent = 'Изменить';
            inputs.forEach(input => input.disabled = true);
            button.disabled = false;
            form.classList.remove('active');
            $(button).html('<i class="fa fa-save"></i> ' + button.textContent);

        } catch (error) {
            console.error('❌ Ошибка:', error);
            modals();
            if (error.responseText) {
                $('#myModal .modal-body').html(error.responseText);
            }
            button.textContent = originalText;
            button.disabled = false;
        }
    }

    /**
     * Показать сообщение в модалке
     */
    function showModalMessage(message, type = 'success') {
        console.log('📢 Сообщение:', message);
        const modal = $('#myModal');
        if (!modal.length) {
            alert(message);
            return;
        }

        const modalBody = modal.find('.modal-body');
        modalBody.empty();

        const icon = type === 'success' ? '✅' : '❌';
        const messageClass = type === 'success' ? 'text-success' : 'text-danger';

        modalBody.html(`
            <div style="text-align: center; padding: 10px;">
                <div style="font-size: 48px; margin-bottom: 10px;">${icon}</div>
                <p class="${messageClass}" style="margin: 0; font-size: 16px;">${message}</p>
            </div>
        `);

        modal.modal('show');
        setTimeout(() => {
            if (modal.hasClass('show')) modal.modal('hide');
        }, 3000);
    }

    /**
     * Открыть модалку
     * C3 FIX: off().on() вместо on() для предотвращения дублирования обработчиков
     */
    function modals() {
        $('#myModal').modal("show");
        $('.btn-close-custom, .btn-secondary').off('click').on('click', function() {
            $('#myModal').modal('hide');
        });
        $('.btn-primary').attr('disabled', true);
    }

    // Экспорт в глобальную область
    window.FormsModule = {
        submitForm: submitForm,
        getFragmentInfo: getFragmentInfo,
        showModalMessage: showModalMessage,
        modals: modals
    };

    // Глобальные функции для HTML onsubmit/onclick
    window.submitForm = submitForm;
    window.getFragmentInfo = getFragmentInfo;
    window.showModalMessage = showModalMessage;
    window.modals = modals;
})(window);
