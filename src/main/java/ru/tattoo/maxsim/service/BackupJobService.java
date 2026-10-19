package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Синхронный фасад запуска бэкапа: занимает слот в реестре и отдаёт работу воркеру.
 * Так двойной клик или одновременный cron+кнопка не создают два дампа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackupJobService {

    private final BackupWorker worker;
    private final BackupRunRegistry registry;

    /**
     * @return true, если бэкап запущен; false — если уже выполняется
     */
    public boolean start() {
        if (!registry.start()) {
            return false;
        }
        try {
            worker.run();
        } catch (RuntimeException e) {
            // Например, переполнена очередь executor'а: не оставляем статус «выполняется» навсегда
            registry.failed("Не удалось запустить бэкап: " + e.getMessage());
            throw e;
        }
        return true;
    }

    public BackupRunRegistry.Snapshot status() {
        return registry.current();
    }

    public boolean isRunning() {
        return registry.isRunning();
    }
}
