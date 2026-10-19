package ru.tattoo.maxsim.service;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.test.util.ReflectionTestUtils;
import ru.tattoo.maxsim.config.StorageProperties;
import ru.tattoo.maxsim.exceptions.BackupException;
import ru.tattoo.maxsim.model.BackupSettings;
import ru.tattoo.maxsim.model.DTO.BackupResult;
import ru.tattoo.maxsim.model.DTO.DiskItem;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BackupServiceTest {

    private final BackupSettingsService settingsService = mock(BackupSettingsService.class);
    private final YandexDiskService yandexDiskService = mock(YandexDiskService.class);
    private final CryptoService cryptoService = mock(CryptoService.class);
    private final StorageProperties storageProperties = new StorageProperties();
    private final DataSourceProperties dataSourceProperties = new DataSourceProperties();

    private BackupService newService() {
        BackupService service = new BackupService(
            settingsService, yandexDiskService, storageProperties, cryptoService, dataSourceProperties);
        ReflectionTestUtils.setField(service, "dumpTimeoutMinutes", 2);
        ReflectionTestUtils.setField(service, "includeImages", true);
        return service;
    }

    @Test
    void archiveImagesShouldPackFilesWithImagesPrefix(@TempDir Path tempDir) throws Exception {
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir.resolve("2026/10"));
        Files.writeString(imagesDir.resolve("2026/10/photo.png"), "png-bytes");
        storageProperties.setUploadDir(imagesDir.toString());

        Path archive = tempDir.resolve("images.tar.gz");
        Path result = newService().archiveImages(archive);

        assertThat(result).isEqualTo(archive);
        assertThat(collectEntries(archive))
            .containsExactly("images/2026/10/photo.png");
    }

    @Test
    void archiveImagesShouldReturnNullWhenDirectoryMissing(@TempDir Path tempDir) {
        storageProperties.setUploadDir(tempDir.resolve("absent").toString());

        assertThat(newService().archiveImages(tempDir.resolve("images.tar.gz"))).isNull();
    }

    @Test
    void createBackupShouldDumpDatabaseAndUploadBothFiles(@TempDir Path tempDir) throws Exception {
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Files.writeString(imagesDir.resolve("photo.png"), "png-bytes");
        storageProperties.setUploadDir(imagesDir.toString());

        BackupSettings settings = new BackupSettings();
        settings.setBackupDir("/tattoo-backups");
        settings.setYandexToken("encrypted-token");

        when(settingsService.getOrCreate()).thenReturn(settings);
        when(cryptoService.decrypt("encrypted-token")).thenReturn("oauth-token");
        when(yandexDiskService.listFiles("oauth-token", "/tattoo-backups")).thenReturn(List.of());

        dataSourceProperties.setUrl("jdbc:mysql://db:3306/site?useSSL=false");
        dataSourceProperties.setUsername("site_user");
        dataSourceProperties.setPassword("secret");

        BackupService service = newService();
        ReflectionTestUtils.setField(service, "mysqldumpPath", fakeMysqldump(tempDir).toString());

        BackupResult result = service.createBackup();

        verify(yandexDiskService).createFolder("oauth-token", "/tattoo-backups");

        List<String> diskPaths = new ArrayList<>();
        ArgumentCaptor<Path> localFiles = ArgumentCaptor.forClass(Path.class);
        ArgumentCaptor<String> paths = ArgumentCaptor.forClass(String.class);
        verify(yandexDiskService, org.mockito.Mockito.times(2))
            .uploadFile(eq("oauth-token"), localFiles.capture(), paths.capture());
        diskPaths.addAll(paths.getAllValues());

        assertThat(result.dbFile()).startsWith("db_").endsWith(".sql.gz");
        assertThat(result.imagesFile()).startsWith("images_").endsWith(".tar.gz");
        assertThat(result.dbSize()).isPositive();

        assertThat(diskPaths.get(0)).isEqualTo("/tattoo-backups/" + result.dbFile());
        assertThat(diskPaths.get(1)).isEqualTo("/tattoo-backups/" + result.imagesFile());

        // локальные файлы удалены после загрузки
        assertThat(localFiles.getAllValues()).allSatisfy(file -> assertThat(Files.exists(file)).isFalse());

        verify(settingsService).markBackupSuccess();
        verify(settingsService, never()).markBackupFailed();
    }

    @Test
    void rotateRemoteBackupsShouldKeepOnlyLatestPairs() {
        BackupService service = newService();
        ReflectionTestUtils.setField(service, "keepBackups", 2);

        List<DiskItem> items = new ArrayList<>();
        for (String stamp : List.of(
            "2026-01-01_03-00-00", "2026-01-02_03-00-00", "2026-01-03_03-00-00",
            "2026-01-04_03-00-00", "2026-01-05_03-00-00")) {
            items.add(diskItem("db_" + stamp + ".sql.gz", "file"));
            items.add(diskItem("images_" + stamp + ".tar.gz", "file"));
        }
        items.add(diskItem("readme.txt", "file"));
        items.add(diskItem("nested-folder", "dir"));
        when(yandexDiskService.listFiles("token", "/tattoo-backups")).thenReturn(items);

        service.rotateRemoteBackups("token", "/tattoo-backups");

        ArgumentCaptor<String> deletedPaths = ArgumentCaptor.forClass(String.class);
        verify(yandexDiskService, times(6)).delete(eq("token"), deletedPaths.capture());

        List<String> paths = deletedPaths.getAllValues();
        assertThat(paths).noneMatch(path -> path.contains("2026-01-05"));
        assertThat(paths).noneMatch(path -> path.contains("2026-01-04"));
        assertThat(paths).noneMatch(path -> path.contains("readme"));
        assertThat(paths).anyMatch(path -> path.endsWith("db_2026-01-01_03-00-00.sql.gz"));
    }

    @Test
    void rotateRemoteBackupsShouldSkipWhenListEmpty() {
        BackupService service = newService();
        when(yandexDiskService.listFiles("token", "/tattoo-backups")).thenReturn(List.of());

        service.rotateRemoteBackups("token", "/tattoo-backups");

        verify(yandexDiskService, never()).delete(anyString(), anyString());
    }

    @Test
    void rotateRemoteBackupsShouldNotFailBackupWhenDiskUnavailable() {
        BackupService service = newService();
        when(yandexDiskService.listFiles("token", "/tattoo-backups"))
            .thenThrow(new RuntimeException("Диск недоступен"));

        service.rotateRemoteBackups("token", "/tattoo-backups");

        verify(yandexDiskService, never()).delete(anyString(), anyString());
    }

    private DiskItem diskItem(String name, String type) {
        DiskItem item = new DiskItem();
        item.setName(name);
        item.setType(type);
        return item;
    }

    @Test
    void resolveMysqldumpShouldPreferExplicitlyConfiguredPath() {
        BackupService service = newService();
        ReflectionTestUtils.setField(service, "mysqldumpPath", "/opt/custom/mysqldump");

        assertThat(service.resolveMysqldump()).isEqualTo("/opt/custom/mysqldump");
    }

    @Test
    void createBackupShouldFailWhenTokenMissing() {
        BackupSettings settings = new BackupSettings();
        settings.setBackupDir("/tattoo-backups");
        when(settingsService.getOrCreate()).thenReturn(settings);

        assertThatThrownBy(() -> newService().createBackup())
            .isInstanceOf(BackupException.class)
            .hasMessageContaining("токен");

        verify(settingsService).markBackupFailed();
        verify(yandexDiskService, never()).uploadFile(anyString(), any(Path.class), anyString());
    }

    @Test
    void createBackupShouldMarkFailureAndRethrowWhenUploadFails(@TempDir Path tempDir) throws Exception {
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        storageProperties.setUploadDir(imagesDir.toString());

        BackupSettings settings = new BackupSettings();
        settings.setBackupDir("/tattoo-backups");
        settings.setYandexToken("encrypted-token");
        when(settingsService.getOrCreate()).thenReturn(settings);
        when(cryptoService.decrypt("encrypted-token")).thenReturn("oauth-token");

        dataSourceProperties.setUrl("jdbc:mysql://db:3306/site");
        dataSourceProperties.setUsername("user");
        dataSourceProperties.setPassword("pass");

        doThrow(new RuntimeException("Диск недоступен"))
            .when(yandexDiskService).createFolder(anyString(), anyString());

        BackupService service = newService();
        ReflectionTestUtils.setField(service, "mysqldumpPath", fakeMysqldump(tempDir).toString());

        assertThatThrownBy(service::createBackup)
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Диск недоступен");

        verify(settingsService).markBackupFailed();
    }

    private Path fakeMysqldump(Path tempDir) throws Exception {
        Path script = tempDir.resolve("mysqldump");
        Files.writeString(script, "#!/bin/sh\necho '-- fake dump'\necho 'CREATE TABLE t (id int);'\n");
        Files.setPosixFilePermissions(script, PosixFilePermissions.fromString("rwxr-xr-x"));
        return script;
    }

    private List<String> collectEntries(Path archive) throws Exception {
        List<String> names = new ArrayList<>();
        try (InputStream in = new GZIPInputStream(Files.newInputStream(archive));
             TarArchiveInputStream tar = new TarArchiveInputStream(in)) {
            TarArchiveEntry entry;
            while ((entry = tar.getNextEntry()) != null) {
                names.add(entry.getName());
                if (entry.isFile()) {
                    assertThat(new String(tar.readAllBytes(), StandardCharsets.UTF_8)).isNotBlank();
                }
            }
        }
        return names;
    }
}
