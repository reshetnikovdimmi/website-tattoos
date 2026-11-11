/* ============================================================
   MAIL MODULE - Почта, верификация, копирование
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * Быстрая проверка: только коннект к SMTP (1-2 сек)
     * H2 FIX: убран первый showModalMessage
     */
    async function testSmtpConnection() {
        const form = document.querySelector('.settings-form');
        const containerSelector = form ? form.dataset.containerSelector : '.settings-body';

        if (!form) {
            console.error('Форма не найдена');
            return;
        }

        const formData = new FormData(form);

        try {
            // H1 FIX: CSRF-токен для POST-запросов
            const csrfToken = $('meta[name="_csrf"]').attr('content');
            
            const response = await $.ajax({
                url: '/admin/mail/test-connection',
                type: 'POST',
                dataType: 'html',
                data: formData,
                processData: false,
                contentType: false,
                headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {}
            });

            console.log('🔌 Connection test:', response);

            $(containerSelector).html(response);

            // Читаем результат из скрытого div и показываем модалку
            setTimeout(() => {
                const resultDiv = document.getElementById('smtp-test-result');
                if (resultDiv) {
                    const success = resultDiv.dataset.success === 'true';
                    const error = resultDiv.dataset.error || '';

                    if (success) {
                        showModalMessage('✅ Соединение установлено');
                    } else {
                        showModalMessage('❌ ' + error);
                    }
                }
            }, 100);

        } catch (error) {
            console.error('❌ Connection test error:', error);
            modals();
            if (error.responseText) {
                $('.modal-body').html(error.responseText);
            }
        }
    }

    /**
     * Копировать логи из панели
     */
    function copyLogs() {
        const output = document.getElementById('smtp-log-output');
        if (!output) return;
        navigator.clipboard.writeText(output.textContent).then(() => {
            showModalMessage('📋 Логи скопированы');
        });
    }

    /**
     * Очистить логи
     */
    function clearLogs() {
        const output = document.getElementById('smtp-log-output');
        const badge = document.getElementById('smtp-status-badge');
        const toolbar = document.querySelector('.smtp-log-toolbar');

        if (output) output.textContent = 'Нажмите «Проверить» или «Отправить тест», чтобы увидеть логи...';
        if (badge) badge.style.display = 'none';
        if (toolbar) toolbar.style.display = 'none';
    }

    /**
     * Отправить тестовое письмо
     * H4 FIX: проверка response перед вставкой
     */
    async function sendTestEmail() {
        const form = document.querySelector('.settings-form');
        const testEmailInput = document.getElementById('test-email');
        const testEmail = testEmailInput ? testEmailInput.value.trim() : '';

        const containerSelector = form ? form.dataset.containerSelector : '.settings-body';

        if (!testEmail) {
            modals();
            $('.modal-body').html('<div class="alert alert-warning"><i class="fa fa-exclamation-triangle"></i> Укажите тестовый email</div>');
            testEmailInput && testEmailInput.focus();
            return;
        }

        const formData = new FormData(form);
        formData.append('testEmail', testEmail);

        try {
            // H1 FIX: CSRF-токен для POST-запросов
            const csrfToken = $('meta[name="_csrf"]').attr('content');
            
            const response = await $.ajax({
                url: '/admin/mail/test-send',
                type: 'POST',
                dataType: 'html',
                data: formData,
                processData: false,
                contentType: false,
                headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {}
            });

            console.log('✉️ Send test response received');

            // H4 FIX: проверяем response перед вставкой
            if (response) {
                $(containerSelector).html(response);
            }

            setTimeout(() => {
                const resultDiv = document.getElementById('smtp-test-result');
                if (resultDiv) {
                    const success = resultDiv.dataset.success === 'true';
                    const error = resultDiv.dataset.error || '';

                    if (success) {
                        showModalMessage('✅ Тестовое письмо отправлено');
                    } else {
                        showModalMessage('❌ ' + error);
                    }
                }
            }, 100);

        } catch (error) {
            console.error('❌ Send test error:', error);
            modals();
            if (error.responseText) {
                $('.modal-body').html(error.responseText);
            }
        }
    }

    /**
     * Инициализация формы настроек почты:
     * - автосмена порта при смене SSL/TLS
     * - вызывать после каждой перезагрузки фрагмента
     */
    function initMailSettings() {
        const sslRadio = document.getElementById('mail-ssl');
        const tlsRadio = document.getElementById('mail-tls');
        const portInput = document.getElementById('mail-port');

        // Если мы не на странице настроек почты — выходим
        if (!sslRadio || !tlsRadio || !portInput) return;

        function updatePort() {
            if (sslRadio.checked) {
                portInput.value = 465;
            } else if (tlsRadio.checked) {
                portInput.value = 587;
            }
        }

        // Снимаем старые обработчики (чтобы не дублировать при повторном вызове)
        sslRadio.removeEventListener('change', updatePort);
        tlsRadio.removeEventListener('change', updatePort);

        // Вешаем заново
        sslRadio.addEventListener('change', updatePort);
        tlsRadio.addEventListener('change', updatePort);

        // Устанавливаем правильный порт при инициализации
        updatePort();
    }

    /**
     * Показать/скрыть панель логов при клике на чекбокс "Отладка"
     */
    function toggleDebugPanel() {
        const debugCheckbox = document.getElementById('mail-debug');
        const logPanel = document.getElementById('smtp-log-panel');

        if (!debugCheckbox || !logPanel) return;

        logPanel.style.display = debugCheckbox.checked ? 'block' : 'none';
    }

    /**
     * Переключение вкладки сервиса
     */
    function switchServiceTab(serviceId) {
        document.querySelectorAll('.service-tab-content').forEach(function(el) {
            el.classList.remove('active');
        });
        document.querySelectorAll('.service-tab-btn').forEach(function(el) {
            el.classList.remove('active');
        });
        var tab = document.getElementById('tab-' + serviceId);
        if (tab) tab.classList.add('active');
        var btn = document.querySelector('.service-tab-btn[data-tab="' + serviceId + '"]');
        if (btn) btn.classList.add('active');
    }

    /**
     * Копирование в буфер обмена
     */
    function copyToClipboard(text, btn) {
        if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(text).then(function() {
                showCopied(btn);
            }).catch(function() {
                fallbackCopy(text, btn);
            });
        } else {
            fallbackCopy(text, btn);
        }
    }

    function showCopied(btn) {
        var original = btn.innerHTML;
        btn.innerHTML = '<i class="fa fa-check"></i> Готово';
        btn.classList.add('copied');
        setTimeout(function() {
            btn.innerHTML = original;
            btn.classList.remove('copied');
        }, 2000);
    }

    function fallbackCopy(text, btn) {
        var textarea = document.createElement('textarea');
        textarea.value = text;
        textarea.style.position = 'fixed';
        textarea.style.opacity = '0';
        document.body.appendChild(textarea);
        textarea.select();
        try {
            document.execCommand('copy');
            showCopied(btn);
        } catch (err) {
            console.error('Copy failed', err);
        }
        document.body.removeChild(textarea);
    }

    /**
     * Обновление полей верификации
     */
    function updateVerificationFields() {
        var methodEl = document.getElementById('ver-method');
        if (!methodEl) return; // фрагмент ещё не загружен — выходим

        var method = methodEl.value;
        var meta = document.getElementById('meta-instruction');
        var html = document.getElementById('html-instruction');
        var dns = document.getElementById('dns-instruction');
        var preview = document.getElementById('html-preview');

        // Скрываем все (если они есть)
        if (meta) meta.style.display = 'none';
        if (html) html.style.display = 'none';
        if (dns) dns.style.display = 'none';
        if (preview) preview.style.display = 'none';

        // Показываем нужную
        if (method === 'META_TAG' && meta) {
            meta.style.display = 'block';
        } else if (method === 'HTML_FILE') {
            if (html) html.style.display = 'block';
            if (preview) preview.style.display = 'block';
            updateHtmlPreview();
        } else if (method === 'DNS' && dns) {
            dns.style.display = 'block';
        }
    }

    /**
     * Переключает активность записи верификации (вкл/выкл)
     * @param {HTMLElement} btn — кнопка, на которую нажали
     * @param {number} id — ID записи в БД
     */
    function toggleVerification(btn, id) {
        // Находим контейнер, в котором лежит список
        var container = document.querySelector('.verification-section')
                     || document.querySelector('.verification-list')
                     || btn.closest('.card-body');

        if (!container) {
            console.error('Контейнер верификации не найден');
            return;
        }

        btn.disabled = true;

        // H1 FIX: CSRF-токен для fetch-запросов
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        
        fetch('/admin/verification/toggle/' + id, {
            method: 'POST',
            headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken, 'Content-Type': 'application/x-www-form-urlencoded' } : {}
        })
        .then(function(response) {
            if (!response.ok) throw new Error('Ошибка сервера: ' + response.status);
            return response.text();
        })
        .then(function(html) {
            // Заменяем содержимое контейнера на новый фрагмент от сервера
            container.innerHTML = html;
        })
        .catch(function(err) {
            console.error('Toggle error:', err);
            alert('Не удалось изменить статус: ' + err.message);
            btn.disabled = false;
        });
    }

    // Экспорт в глобальную область
    window.MailModule = {
        testSmtpConnection: testSmtpConnection,
        copyLogs: copyLogs,
        clearLogs: clearLogs,
        sendTestEmail: sendTestEmail,
        initMailSettings: initMailSettings,
        toggleDebugPanel: toggleDebugPanel,
        switchServiceTab: switchServiceTab,
        copyToClipboard: copyToClipboard,
        updateVerificationFields: updateVerificationFields,
        toggleVerification: toggleVerification
    };
})(window);
