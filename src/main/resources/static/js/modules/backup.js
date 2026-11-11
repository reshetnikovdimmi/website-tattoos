/* ============================================================
   BACKUP MODULE - Бэкапы Яндекс.Диск
   ============================================================ */

(function(window) {
    'use strict';

    /**
     * Установить статус бэкапа
     */
    function setBackupStatus(text, cssClass) {
        const status = document.getElementById('backup-test-status');
        if (!status) return;
        status.textContent = text || '';
        status.className = cssClass || '';
    }

    /**
     * Проверка подключения к бэкапу
     */
    async function testBackupConnection() {
        setBackupStatus('Проверка подключения...', 'text-muted');

        try {
            // H1 FIX: CSRF-токен для POST-запросов
            const csrfToken = $('meta[name="_csrf"]').attr('content');
            
            const data = await $.ajax({
                url: '/admin/backup/test',
                type: 'POST',
                dataType: 'json',
                headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {}
            });

            if (data.success) {
                setBackupStatus('Подключение установлено', 'text-success');
                showModalMessage('✅ Подключение установлено');
            } else {
                setBackupStatus('Ошибка подключения', 'text-danger');
                showModalMessage('❌ ' + (data.error || 'Не удалось подключиться'), 'error');
            }
        } catch (e) {
            setBackupStatus('Ошибка подключения', 'text-danger');
            showModalMessage('❌ Ошибка запроса: ' + (e.statusText || e.message), 'error');
        }
    }

    /**
     * Задержка
     */
    function sleep(ms) {
        return new Promise(function (resolve) { setTimeout(resolve, ms); });
    }

    /**
     * Создать бэкап
     */
    async function createBackup() {
        const btn = document.getElementById('backup-create-btn');
        const originalHtml = btn ? btn.innerHTML : '';

        if (btn) {
            btn.disabled = true;
            btn.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Запуск...';
        }
        setBackupStatus('Бэкап запускается...', 'text-muted');

        try {
            // H1 FIX: CSRF-токен для POST-запросов
            const csrfToken = $('meta[name="_csrf"]').attr('content');
            
            const started = await $.ajax({
                url: '/admin/backup/create',
                type: 'POST',
                dataType: 'json',
                headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {}
            });

            if (!started.success) {
                setBackupStatus('Ошибка запуска бэкапа', 'text-danger');
                showModalMessage('❌ ' + (started.error || 'Не удалось запустить бэкап'), 'error');
                return;
            }

            setBackupStatus('Дамп БД и загрузка на Диск...', 'text-muted');
            const finished = await waitForBackup();

            if (finished && finished.state === 'SUCCESS') {
                setBackupStatus('Бэкап создан: ' + (finished.dir || ''), 'text-success');
                showModalMessage('✅ Бэкап создан' + (finished.dir ? ': ' + finished.dir : ''));
                await reloadBackupFragment();
            } else {
                const message = finished && finished.message
                    ? finished.message
                    : 'Бэкап выполняется слишком долго. Проверьте статус позже.';
                setBackupStatus('Ошибка создания бэкапа', 'text-danger');
                showModalMessage('❌ ' + message, 'error');
            }
        } catch (e) {
            setBackupStatus('Ошибка создания бэкапа', 'text-danger');
            showModalMessage('❌ ' + (e.statusText || e.message || 'Ошибка запроса'), 'error');
        } finally {
            if (btn) {
                btn.disabled = false;
                btn.innerHTML = originalHtml;
            }
        }
    }

    /**
     * Опрашивает статус фонового бэкапа, пока он не завершится (до 6 минут).
     * Возвращает последний статус с сервера или null по таймауту.
     */
    async function waitForBackup() {
        const maxChecks = 120;   // 120 * 3с = 6 минут

        for (let i = 0; i < maxChecks; i++) {
            await sleep(3000);

            let status;
            try {
                status = await $.ajax({
                    url: '/admin/backup/status',
                    type: 'GET',
                    dataType: 'json',
                    cache: false
                });
            } catch (e) {
                continue;
            }

            if (status.state !== 'RUNNING' && status.state !== 'IDLE') {
                return status;
            }
            setBackupStatus('Бэкап выполняется... (' + ((i + 1) * 3) + ' с)', 'text-muted');
        }
        return null;
    }

    /**
     * Восстановить бэкап
     */
    async function restoreBackup(stamp) {
        if (!stamp) return;

        const typed = prompt(
            'Восстановление перезапишет текущую БД и картинки из копии ' + stamp + '.\n' +
            'Введите ВОССТАНОВИТЬ для продолжения:'
        );
        if (typed === null) return;
        if (typed.trim().toUpperCase() !== 'ВОССТАНОВИТЬ') {
            showModalMessage('❌ Восстановление отменено: неверное подтверждение', 'error');
            return;
        }

        setBackupStatus('Восстановление из копии ' + stamp + '...', 'text-muted');

        try {
            // H1 FIX: CSRF-токен для POST-запросов
            const csrfToken = $('meta[name="_csrf"]').attr('content');
            
            const started = await $.ajax({
                url: '/admin/backup/restore',
                type: 'POST',
                dataType: 'json',
                data: { stamp: stamp, confirmation: 'RESTORE' },
                headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {}
            });

            if (!started.success) {
                showModalMessage('❌ ' + (started.error || 'Не удалось запустить восстановление'), 'error');
                return;
            }

            const finished = await waitForRestore();
            if (finished && finished.state === 'SUCCESS') {
                showModalMessage('✅ Восстановление завершено');
                await reloadBackupFragment();
            } else {
                const message = finished && finished.message
                    ? finished.message
                    : 'Восстановление выполняется слишком долго. Проверьте статус позже.';
                showModalMessage('❌ ' + message, 'error');
            }
        } catch (e) {
            showModalMessage('❌ ' + (e.statusText || e.message || 'Ошибка запроса'), 'error');
        } finally {
            setBackupStatus('', '');
        }
    }

    /**
     * Ожидание завершения восстановления
     */
    async function waitForRestore() {
        const maxChecks = 200;   // 200 * 3с = 10 минут

        for (let i = 0; i < maxChecks; i++) {
            await sleep(3000);

            let status;
            try {
                status = await $.ajax({
                    url: '/admin/backup/restore/status',
                    type: 'GET',
                    dataType: 'json',
                    cache: false
                });
            } catch (e) {
                continue;
            }

            if (status.state !== 'RUNNING' && status.state !== 'IDLE') {
                return status;
            }
            setBackupStatus('Восстановление... (' + ((i + 1) * 3) + ' с)', 'text-muted');
        }
        return null;
    }

    /**
     * Скачать бэкап
     */
    async function downloadBackup(fileName) {
        try {
            const data = await $.ajax({
                url: '/admin/backup/download',
                type: 'GET',
                data: { fileName: fileName },
                dataType: 'json'
            });

            if (data.success && data.url) {
                window.open(data.url, '_blank');
            } else {
                showModalMessage('❌ ' + (data.error || 'Не удалось получить ссылку'), 'error');
            }
        } catch (e) {
            showModalMessage('❌ Ошибка запроса: ' + (e.statusText || e.message), 'error');
        }
    }

    /**
     * Удалить бэкап
     */
    async function deleteBackup(fileName) {
        if (!confirm('Удалить бэкап ' + fileName + ' с Яндекс.Диска?')) {
            return;
        }

        try {
            // H1 FIX: CSRF-токен для POST-запросов
            const csrfToken = $('meta[name="_csrf"]').attr('content');
            
            const data = await $.ajax({
                url: '/admin/backup/delete',
                type: 'POST',
                data: { fileName: fileName },
                dataType: 'json',
                headers: csrfToken ? { 'X-CSRF-TOKEN': csrfToken } : {}
            });

            if (data.success) {
                showModalMessage('✅ Бэкап удалён');
                await reloadBackupFragment();
            } else {
                showModalMessage('❌ ' + (data.error || 'Не удалось удалить'), 'error');
            }
        } catch (e) {
            showModalMessage('❌ Ошибка запроса: ' + (e.statusText || e.message), 'error');
        }
    }

    /**
     * Перезагружает блок настроек бэкапа с сервера (список файлов, статус последнего бэкапа).
     */
    async function reloadBackupFragment() {
        const container = document.querySelector('.backup-settings');
        if (!container) return;

        try {
            const html = await $.ajax({
                url: '/admin/backup/fragment',
                type: 'GET',
                dataType: 'html',
                cache: false
            });

            const wrapper = document.createElement('div');
            wrapper.innerHTML = html.trim();
            const fresh = wrapper.querySelector('.backup-settings');
            if (fresh) {
                container.replaceWith(fresh);
            }
        } catch (e) {
            console.error('Не удалось обновить блок бэкапов', e);
        }
    }

    // Экспорт в глобальную область
    window.BackupModule = {
        setBackupStatus: setBackupStatus,
        testBackupConnection: testBackupConnection,
        createBackup: createBackup,
        restoreBackup: restoreBackup,
        downloadBackup: downloadBackup,
        deleteBackup: deleteBackup
    };

    // Глобальные функции для HTML onclick
    window.setBackupStatus = setBackupStatus;
    window.testBackupConnection = testBackupConnection;
    window.createBackup = createBackup;
    window.restoreBackup = restoreBackup;
    window.downloadBackup = downloadBackup;
    window.deleteBackup = deleteBackup;
})(window);
