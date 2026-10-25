package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Выполняет восстановление в отдельном потоке (аналогично {@link BackupWorker}).
 *
 * <p>Поток тот же — {@code backupExecutor} с одним рабочим потоком. Поэтому восстановление
 * и создание бэкапа физически не могут идти одновременно, даже если бы кто-то обошёл семафор.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestoreWorker {

    private final RestoreService restoreService;
    private final RestoreRunRegistry registry;

    @Async("backupExecutor")
    public void run(String stamp) {
        try {
            restoreService.restore(stamp);
            registry.success();
            log.info("Восстановление из копии {} завершено", stamp);
        } catch (Exception e) {
            registry.failed(e.getMessage());
            log.error("Восстановление из копии {} не удалось: {}", stamp, e.getMessage(), e);
        }
    }
}
