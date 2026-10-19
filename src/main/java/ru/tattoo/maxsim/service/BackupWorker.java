package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.tattoo.maxsim.model.DTO.BackupResult;

/**
 * Асинхронное выполнение бэкапа в отдельном потоке,
 * чтобы HTTP-запрос не висел минутами и не упирался в таймаут nginx.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BackupWorker {

    private final BackupService backupService;
    private final BackupRunRegistry registry;

    @Async("backupExecutor")
    public void run() {
        try {
            BackupResult result = backupService.createBackup();
            registry.success(result.diskDir());
            log.info("Бэкап завершён: {} ({} байт)", result.diskDir(), result.totalSize());
        } catch (Exception e) {
            registry.failed(e.getMessage());
            log.error("Бэкап не удался: {}", e.getMessage(), e);
        }
    }
}
