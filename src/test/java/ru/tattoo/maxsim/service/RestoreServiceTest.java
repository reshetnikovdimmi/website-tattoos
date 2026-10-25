package ru.tattoo.maxsim.service;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.test.util.ReflectionTestUtils;
import ru.tattoo.maxsim.config.StorageProperties;
import ru.tattoo.maxsim.exceptions.BackupException;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class RestoreServiceTest {

    private final BackupSettingsService settingsService = mock(BackupSettingsService.class);
    private final YandexDiskService yandexDiskService = mock(YandexDiskService.class);
    private final CryptoService cryptoService = mock(CryptoService.class);
    private final StorageProperties storageProperties = new StorageProperties();
    private final DataSourceProperties dataSourceProperties = new DataSourceProperties();
    private final BackupOperationLock operationLock = new BackupOperationLock();

    private RestoreService newService() {
        return new RestoreService(settingsService, yandexDiskService, storageProperties,
            cryptoService, dataSourceProperties, operationLock);
    }

    @Test
    void shouldRejectInvalidStamp() {
        assertThatThrownBy(() -> newService().restore("../../etc/passwd"))
            .isInstanceOf(BackupException.class)
            .hasMessageContaining("Некорректная дата копии");
    }

    @Test
    void shouldRejectWhenBackupOrRestoreAlreadyRunning() {
        assertThat(operationLock.tryLock()).isTrue();
        try {
            assertThatThrownBy(() -> newService().restore("2026-01-01_03-00-00"))
                .isInstanceOf(BackupException.class)
                .hasMessageContaining("Уже выполняется");
        } finally {
            operationLock.unlock();
        }
    }

    @Test
    void restoreImagesShouldWriteFilesIntoUploadDir(@TempDir Path tempDir) throws Exception {
        Path uploadDir = tempDir.resolve("uploads");
        storageProperties.setUploadDir(uploadDir.toString());

        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("images/2026/10/photo.png", "png-bytes");
        Path archive = tarGz(tempDir, entries);

        newService().restoreImages(archive);

        Path restored = uploadDir.resolve("2026/10/photo.png");
        assertThat(restored).exists();
        assertThat(Files.readString(restored)).isEqualTo("png-bytes");
    }

    @Test
    void restoreImagesShouldIgnoreEntriesOutsideImagesDir(@TempDir Path tempDir) throws Exception {
        Path uploadDir = tempDir.resolve("uploads");
        storageProperties.setUploadDir(uploadDir.toString());

        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("other/secret.txt", "nope");
        entries.put("images/ok.png", "ok");
        Path archive = tarGz(tempDir, entries);

        newService().restoreImages(archive);

        assertThat(uploadDir.resolve("ok.png")).exists();
        assertThat(uploadDir.resolve("secret.txt")).doesNotExist();
        assertThat(tempDir.resolve("secret.txt")).doesNotExist();
    }

    @Test
    void restoreImagesShouldRejectPathTraversal(@TempDir Path tempDir) throws Exception {
        Path uploadDir = tempDir.resolve("uploads");
        storageProperties.setUploadDir(uploadDir.toString());

        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("images/../../evil.txt", "evil");
        Path archive = tarGz(tempDir, entries);

        assertThatThrownBy(() -> newService().restoreImages(archive))
            .isInstanceOf(BackupException.class)
            .hasMessageContaining("Недопустимый путь в архиве");

        assertThat(tempDir.resolve("evil.txt")).doesNotExist();
    }

    @Test
    void resolveMysqlShouldPreferExplicitPath() {
        RestoreService service = newService();
        ReflectionTestUtils.setField(service, "mysqlPath", "/opt/custom/mysql");

        assertThat(service.resolveMysql()).isEqualTo("/opt/custom/mysql");
    }

    private Path tarGz(Path dir, Map<String, String> entries) throws Exception {
        Path archive = dir.resolve("images.tar.gz");
        try (OutputStream out = Files.newOutputStream(archive);
             GZIPOutputStream gzip = new GZIPOutputStream(out);
             TarArchiveOutputStream tar = new TarArchiveOutputStream(gzip)) {

            for (Map.Entry<String, String> entry : entries.entrySet()) {
                byte[] content = entry.getValue().getBytes(StandardCharsets.UTF_8);
                TarArchiveEntry tarEntry = new TarArchiveEntry(entry.getKey());
                tarEntry.setSize(content.length);
                tar.putArchiveEntry(tarEntry);
                tar.write(content);
                tar.closeArchiveEntry();
            }
        }
        return archive;
    }
}
