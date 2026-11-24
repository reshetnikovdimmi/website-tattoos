package ru.tattoo.maxsim.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Периодически удаляет файлы, на которые больше не ссылается БД.
 *
 * <p>Файл удаляется только если он старше retention-периода. Это защищает
 * от гонки «файл сохранён, транзакция БД ещё не закоммичена».
 */
@Service
@Slf4j
public class OrphanImageCleanupService {

    private final ImageStorage imageStorage;
    private final ImageReferenceService imageReferenceService;
    private final Duration retention;

    public OrphanImageCleanupService(ImageStorage imageStorage,
                                     ImageReferenceService imageReferenceService,
                                     @Value("${app.storage.orphan-retention:PT24H}") Duration retention) {
        this.imageStorage = imageStorage;
        this.imageReferenceService = imageReferenceService;
        this.retention = retention;
    }

    @Scheduled(cron = "${app.storage.orphan-cleanup-cron:0 0 4 * * *}")
    public void scheduledCleanup() {
        try {
            CleanupResult result = cleanupOrphans(retention);
            log.info("Очистка сирот завершена: {}", result);
        } catch (Exception e) {
            log.error("Очистка сирот не выполнена: {}", e.getMessage(), e);
        }
    }

    public CleanupResult cleanupOrphans(Duration olderThan) throws IOException {
        Duration age = olderThan == null || olderThan.isNegative() ? Duration.ZERO : olderThan;
        Instant cutoff = Instant.now().minus(age);

        Set<String> referenced = imageReferenceService.collectReferencedImageNames();
        List<String> files = imageStorage.listImages();

        int retained = 0;
        int deleted = 0;
        int errors = 0;

        for (String fileName : files) {
            if (fileName == null || fileName.isBlank()) {
                continue;
            }
            if (referenced.contains(fileName)) {
                retained++;
                continue;
            }

            Instant modified;
            try {
                modified = imageStorage.lastModified(fileName);
            } catch (IOException e) {
                errors++;
                log.warn("Не удалось получить дату файла {}: {}", fileName, e.getMessage());
                continue;
            }

            if (!modified.isBefore(cutoff)) {
                retained++;
                continue;
            }

            try {
                imageStorage.deleteImage(fileName);
                deleted++;
                log.info("Удалён файл-сирота: {}", fileName);
            } catch (IOException e) {
                errors++;
                log.warn("Не удалось удалить файл-сироту {}: {}", fileName, e.getMessage());
            }
        }

        return new CleanupResult(files.size(), deleted, retained, errors);
    }

    public record CleanupResult(int scanned, int deleted, int retained, int errors) {
    }
}
