package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tattoo.maxsim.model.BackupSettings;
import ru.tattoo.maxsim.model.DTO.DiskItem;
import ru.tattoo.maxsim.repository.BackupSettingsRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Работа с единственной строкой настроек бэкапа в таблице {@code backup_settings}.
 *
 * <p>Что здесь важно понять:
 * <ul>
 *   <li>настройки хранятся в БД, а не в файле — их можно менять из админки без перезапуска;</li>
 *   <li>токен Яндекс.Диска <b>не хранится в открытом виде</b>: при сохранении он шифруется
 *       {@link CryptoService}, а при использовании расшифровывается;</li>
 *   <li>{@code @Transactional} — метод выполняется в транзакции: если что-то упадёт,
 *       изменения в БД откатятся целиком.</li>
 * </ul>
 *
 * <p>Методы можно разделить на три группы: настройки ({@code getOrCreate}, {@code save}),
 * общение с Диском ({@code testConnection}, {@code listBackups}, {@code deleteBackup},
 * {@code getDownloadUrl}) и статус прогона ({@code markBackupSuccess/Failed}).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackupSettingsService {

    private static final String PROVIDER_YANDEX = "YANDEX_DISK";

    private final BackupSettingsRepository repository;
    private final CryptoService cryptoService;
    private final YandexDiskService yandexDiskService;

    @Transactional
    public BackupSettings getOrCreate() {
        return repository.findByProvider(PROVIDER_YANDEX)
            .orElseGet(() -> {
                BackupSettings fresh = new BackupSettings();
                fresh.setProvider(PROVIDER_YANDEX);
                fresh.setBackupDir("/tattoo-backups");
                fresh.setEnabled(true);
                return repository.save(fresh);
            });
    }

    /**
     * Сохраняет настройки из формы админки.
     *
     * <p>Тонкость: пустое поле токена означает «не менять». Иначе при каждом сохранении
     * формы (например, поменяли только папку) токен бы стирался.
     */
    @Transactional
    public BackupSettings save(BackupSettings form) {
        BackupSettings existing = getOrCreate();

        existing.setBackupDir(form.getBackupDir());
        existing.setEnabled(form.getEnabled() != null && form.getEnabled());

        String rawToken = form.getYandexToken();
        if (rawToken != null && !rawToken.isBlank()) {
            // В БД кладём только шифротекст. Открытый токен нигде не сохраняется.
            existing.setYandexToken(cryptoService.encrypt(rawToken));
        }

        return repository.save(existing);
    }

    public boolean testConnection() {
        BackupSettings settings = getOrCreate();
        if (settings.getYandexToken() == null || settings.getYandexToken().isBlank()) {
            log.warn("Тест подключения: токен не задан");
            return false;
        }
        String token = cryptoService.decrypt(settings.getYandexToken());
        return yandexDiskService.testConnection(token);
    }

    public List<DiskItem> listBackups() {
        BackupSettings settings = getOrCreate();
        if (settings.getYandexToken() == null || settings.getYandexToken().isBlank()) {
            return List.of();
        }
        String token = cryptoService.decrypt(settings.getYandexToken());
        return yandexDiskService.listFiles(token, settings.getBackupDir());
    }

    public void deleteBackup(String fileName) {
        BackupSettings settings = getOrCreate();
        if (settings.getYandexToken() == null || settings.getYandexToken().isBlank()) {
            throw new IllegalStateException("Токен Яндекс.Диска не задан");
        }
        String token = cryptoService.decrypt(settings.getYandexToken());
        String fullPath = settings.getBackupDir() + "/" + fileName;
        yandexDiskService.delete(token, fullPath);
    }

    public String getDownloadUrl(String fileName) {
        BackupSettings settings = getOrCreate();
        String token = cryptoService.decrypt(settings.getYandexToken());
        String fullPath = settings.getBackupDir() + "/" + fileName;
        return yandexDiskService.getDownloadUrl(token, fullPath);
    }

    /** Записать в БД, что последний прогон завершился успешно. */
    @Transactional
    public void markBackupSuccess() {
        updateStatus("SUCCESS");
    }

    /** Записать в БД, что последний прогон завершился ошибкой. */
    @Transactional
    public void markBackupFailed() {
        updateStatus("FAILED");
    }

    private void updateStatus(String status) {
        BackupSettings settings = getOrCreate();
        settings.setLastBackupAt(LocalDateTime.now());
        settings.setLastBackupStatus(status);
        settings.setUpdatedAt(LocalDateTime.now());
        repository.save(settings);
    }
}
