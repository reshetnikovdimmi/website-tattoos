package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Фасад запуска восстановления: атомарно занимает слот и отдаёт работу воркеру.
 * По аналогии с {@link BackupJobService} — контроллер не должен сам решать,
 * можно ли начинать операцию.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreJobService {

    private final RestoreWorker worker;
    private final RestoreRunRegistry registry;

    /**
     * @return true, если восстановление запущено; false — если уже выполняется
     */
    public boolean start(String stamp) {
        if (!registry.start(stamp)) {
            return false;
        }
        try {
            worker.run(stamp);
        } catch (RuntimeException e) {
            registry.failed("Не удалось запустить восстановление: " + e.getMessage());
            throw e;
        }
        return true;
    }

    public RestoreRunRegistry.Snapshot status() {
        return registry.current();
    }

    public boolean isRunning() {
        return registry.isRunning();
    }
}
