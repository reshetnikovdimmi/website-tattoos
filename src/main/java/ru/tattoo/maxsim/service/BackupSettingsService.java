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

    @Transactional
    public BackupSettings save(BackupSettings form) {
        BackupSettings existing = getOrCreate();

        existing.setBackupDir(form.getBackupDir());
        existing.setEnabled(form.getEnabled() != null && form.getEnabled());

        String rawToken = form.getYandexToken();
        if (rawToken != null && !rawToken.isBlank()) {
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

    @Transactional
    public void markBackupSuccess() {
        updateStatus("SUCCESS");
    }

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
