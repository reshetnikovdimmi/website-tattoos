package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.tattoo.maxsim.model.DTO.BackupResult;

/**
 * Выполняет бэкап в отдельном потоке.
 *
 * <p><b>Зачем отдельный класс.</b> {@code @Async} работает только тогда, когда метод вызывается
 * у <em>прокси-объекта</em>, а не у самого себя. Если бы мы поставили {@code @Async} на метод
 * внутри {@link BackupJobService} и вызвали его из другого метода того же класса, Spring
 * не смог бы перехватить вызов и код выполнился бы синхронно. Отдельный бин {@code BackupWorker}
 * гарантирует, что вызов идёт «снаружи» и действительно становится фоновым.
 *
 * <p><b>Почему здесь try/catch.</b> Исключение в асинхронном потоке не долетит до контроллера —
 * его некому вернуть. Поэтому воркер сам ловит ошибку, записывает её в реестр статусов
 * ({@link BackupRunRegistry#failed(String)}) и в лог, а не «теряет».
 *
 * <p>{@code @Async("backupExecutor")} — используй пул потоков с именем {@code backupExecutor}
 * (он настроен в {@link ru.tattoo.maxsim.config.AsyncSchedulingConfig}: один поток, чтобы
 * два бэкапа не шли параллельно).
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
