package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.config.StorageProperties;
import ru.tattoo.maxsim.exceptions.BackupException;
import ru.tattoo.maxsim.model.BackupSettings;
import ru.tattoo.maxsim.model.DTO.BackupResult;
import ru.tattoo.maxsim.model.DTO.DiskItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

/**
 * Создание offsite-бэкапа: дамп БД + архив картинок + загрузка на Яндекс.Диск.
 *
 * <p>Дамп делается утилитой {@code mysqldump} (в Docker-образе установлен клиент MySQL),
 * архив картинок — tar.gz по папке {@code app.storage.upload-dir}.
 * Оба файла кладутся в одну папку Диска с общим префиксом-датой,
 * чтобы БД и картинки от одного прогона было легко сопоставить.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private static final DateTimeFormatter STAMP =
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final Pattern MYSQL_URL =
        Pattern.compile("^jdbc:mysql://([^:/?]+)(?::(\\d+))?/([^?;]+)");
    private static final String DEFAULT_BACKUP_DIR = "/tattoo-backups";

    /** Файлы бэкапа: db_2026-01-01_03-00-00.sql.gz / images_....tar.gz */
    private static final Pattern BACKUP_FILE = Pattern.compile(
        "^(db|images)_(\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2})\\.(?:sql|tar)\\.gz$");

    private final BackupSettingsService settingsService;
    private final YandexDiskService yandexDiskService;
    private final StorageProperties storageProperties;
    private final CryptoService cryptoService;
    private final DataSourceProperties dataSourceProperties;

    /** Путь к mysqldump. Пусто — искать в PATH (mysqldump / mariadb-dump). */
    @Value("${app.backup.mysqldump-path:}")
    private String mysqldumpPath = "";

    @Value("${app.backup.dump-timeout-minutes:15}")
    private int dumpTimeoutMinutes = 15;

    @Value("${app.backup.include-images:true}")
    private boolean includeImages = true;

    /** Сколько последних бэкапов хранить на Диске (0 — не ротировать). */
    @Value("${app.backup.keep:14}")
    private int keepBackups = 14;

    /** Защита от двух одновременных дампов (кнопка + планировщик). */
    private final ReentrantLock runLock = new ReentrantLock();

    /**
     * Делает бэкап и загружает файлы на Диск.
     *
     * @return имена загруженных файлов и их размеры
     * @throws BackupException если дамп/архив не удался
     * @throws ru.tattoo.maxsim.exceptions.YandexDiskException если Диск недоступен или токен неверный
     */
    public BackupResult createBackup() {
        if (!runLock.tryLock()) {
            throw new BackupException("Бэкап уже выполняется — дождитесь завершения");
        }
        try {
            return doCreateBackup();
        } finally {
            runLock.unlock();
        }
    }

    private BackupResult doCreateBackup() {
        Path tempDir = null;
        try {
            BackupSettings settings = settingsService.getOrCreate();
            String token = decryptToken(settings);

            String stamp = LocalDateTime.now().format(STAMP);
            String diskDir = normalizeDir(settings.getBackupDir());
            String dbName = "db_" + stamp + ".sql.gz";
            String imagesName = "images_" + stamp + ".tar.gz";

            tempDir = Files.createTempDirectory("tattoo-backup-");
            Path dbFile = dumpDatabase(tempDir.resolve(dbName));
            Path imagesFile = includeImages ? archiveImages(tempDir.resolve(imagesName)) : null;

            yandexDiskService.createFolder(token, diskDir);
            yandexDiskService.uploadFile(token, dbFile, diskDir + "/" + dbName);
            if (imagesFile != null) {
                yandexDiskService.uploadFile(token, imagesFile, diskDir + "/" + imagesName);
            }

            rotateRemoteBackups(token, diskDir);
            settingsService.markBackupSuccess();
            long dbSize = Files.size(dbFile);
            long imagesSize = imagesFile == null ? 0 : Files.size(imagesFile);
            log.info("Бэкап загружен в {}: {}, {} ({} байт)", diskDir, dbName, imagesName,
                dbSize + imagesSize);

            return new BackupResult(diskDir, dbName,
                imagesFile == null ? null : imagesName, dbSize, imagesSize);

        } catch (IOException e) {
            settingsService.markBackupFailed();
            throw new BackupException("Ошибка ввода-вывода при создании бэкапа: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            settingsService.markBackupFailed();
            throw e;
        } finally {
            deleteRecursively(tempDir);
        }
    }

    /**
     * Дамп БД в gzip через mysqldump. Пароль передаётся через MYSQL_PWD,
     * чтобы не светить его в списке процессов.
     */
    Path dumpDatabase(Path targetGz) {
        String jdbcUrl = dataSourceProperties.getUrl();
        Matcher matcher = MYSQL_URL.matcher(jdbcUrl == null ? "" : jdbcUrl);
        if (!matcher.find()) {
            throw new BackupException("Не удалось разобрать spring.datasource.url: " + jdbcUrl);
        }

        String host = matcher.group(1);
        String port = matcher.group(2) != null ? matcher.group(2) : "3306";
        String database = matcher.group(3);
        String username = dataSourceProperties.getUsername();
        String password = dataSourceProperties.getPassword();

        Path plainDump = targetGz.resolveSibling("db.sql");
        String dumpBinary = resolveMysqldump();

        List<String> command = new ArrayList<>(List.of(
            dumpBinary,
            "--host=" + host,
            "--port=" + port,
            "--user=" + username,
            "--single-transaction",
            "--quick",
            "--routines",
            "--triggers",
            "--no-tablespaces",
            "--default-character-set=utf8mb4",
            database));

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectOutput(plainDump.toFile());
        builder.redirectErrorStream(false);
        if (password != null && !password.isBlank()) {
            builder.environment().put("MYSQL_PWD", password);
        }

        try {
            Process process = builder.start();
            String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

            if (!process.waitFor(dumpTimeoutMinutes, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                throw new BackupException(
                    "mysqldump не завершился за " + dumpTimeoutMinutes + " мин");
            }
            if (process.exitValue() != 0) {
                throw new BackupException("mysqldump завершился с кодом " + process.exitValue()
                    + (stderr.isBlank() ? "" : ": " + stderr.trim()));
            }
            if (Files.size(plainDump) == 0) {
                throw new BackupException("mysqldump вернул пустой дамп — проверьте доступ к БД");
            }

            gzip(plainDump, targetGz);
            return targetGz;

        } catch (IOException e) {
            throw new BackupException(
                "Не удалось запустить " + dumpBinary + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BackupException("Дамп БД прерван", e);
        } finally {
            deleteQuietly(plainDump);
        }
    }

    /**
     * Архив папки с картинками в tar.gz. Если папки нет — возвращает null (бэкап уйдёт без картинок).
     */
    Path archiveImages(Path target) {
        Path imagesDir = Paths.get(storageProperties.getUploadDir());
        if (!Files.isDirectory(imagesDir)) {
            log.warn("Папка с картинками не найдена: {}", imagesDir.toAbsolutePath());
            return null;
        }

        try (OutputStream fileOut = Files.newOutputStream(target);
             GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
             TarArchiveOutputStream tar = new TarArchiveOutputStream(gzipOut)) {

            tar.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

            try (Stream<Path> walk = Files.walk(imagesDir)) {
                for (Path file : walk.filter(Files::isRegularFile).toList()) {
                    String entryName = "images/" + imagesDir.relativize(file).toString()
                        .replace(File.separatorChar, '/');

                    tar.putArchiveEntry(new TarArchiveEntry(file.toFile(), entryName));
                    Files.copy(file, tar);
                    tar.closeArchiveEntry();
                }
            }
            return target;

        } catch (IOException e) {
            throw new BackupException(
                "Не удалось заархивировать картинки: " + e.getMessage(), e);
        }
    }

    /**
     * Ищет утилиту дампа: сначала явно заданный путь, затем стандартные имена в PATH.
     * Явное указание нужно, например, для обёртки над {@code docker exec}.
     */
    String resolveMysqldump() {
        if (mysqldumpPath != null && !mysqldumpPath.isBlank()) {
            return mysqldumpPath.trim();
        }

        for (String candidate : List.of(
            "mysqldump", "mariadb-dump",
            "/usr/bin/mysqldump", "/usr/local/bin/mysqldump",
            "/usr/bin/mariadb-dump", "/opt/homebrew/bin/mysqldump")) {
            if (isExecutable(candidate)) {
                log.debug("Для дампа БД используется {}", candidate);
                return candidate;
            }
        }

        throw new BackupException(
            "mysqldump не найден. Установите клиент MySQL (Debian/Ubuntu: "
                + "sudo apt install mysql-client; macOS: brew install mysql-client) "
                + "или задайте путь в app.backup.mysqldump-path. "
                + "В Docker-образе клиент уже установлен.");
    }

    private boolean isExecutable(String candidate) {
        if (candidate.contains("/")) {
            return Files.isExecutable(Paths.get(candidate));
        }
        String path = System.getenv("PATH");
        if (path == null || path.isBlank()) {
            return false;
        }
        for (String dir : path.split(File.pathSeparator)) {
            if (!dir.isBlank() && Files.isExecutable(Paths.get(dir, candidate))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Оставляет на Диске последние {@code keepBackups} бэкапов (пара db+images = один бэкап),
     * остальные удаляет. Ошибки удаления только логируются: бэкап уже создан.
     */
    void rotateRemoteBackups(String token, String diskDir) {
        if (keepBackups <= 0) {
            return;
        }

        List<DiskItem> items;
        try {
            items = yandexDiskService.listFiles(token, diskDir);
        } catch (Exception e) {
            log.warn("Ротация: не удалось получить список бэкапов — {}", e.getMessage());
            return;
        }
        if (items == null || items.isEmpty()) {
            return;
        }

        Map<String, List<DiskItem>> byStamp = new TreeMap<>(Comparator.reverseOrder());
        for (DiskItem item : items) {
            if (item.getName() == null || "dir".equals(item.getType())) {
                continue;
            }
            Matcher matcher = BACKUP_FILE.matcher(item.getName());
            if (matcher.matches()) {
                byStamp.computeIfAbsent(matcher.group(2), stamp -> new ArrayList<>()).add(item);
            }
        }

        int index = 0;
        for (Map.Entry<String, List<DiskItem>> entry : byStamp.entrySet()) {
            index++;
            if (index <= keepBackups) {
                continue;
            }
            for (DiskItem item : entry.getValue()) {
                try {
                    yandexDiskService.delete(token, diskDir + "/" + item.getName());
                    log.info("Ротация: удалён старый бэкап {}", item.getName());
                } catch (Exception e) {
                    log.warn("Ротация: не удалось удалить {} — {}", item.getName(), e.getMessage());
                }
            }
        }
    }

    private void gzip(Path source, Path target) throws IOException {
        try (InputStream in = Files.newInputStream(source);
             OutputStream out = new GZIPOutputStream(Files.newOutputStream(target))) {
            in.transferTo(out);
        }
    }

    private String decryptToken(BackupSettings settings) {
        String encrypted = settings.getYandexToken();
        if (encrypted == null || encrypted.isBlank()) {
            throw new BackupException(
                "OAuth-токен Яндекс.Диска не задан — сохраните его в настройках");
        }
        return cryptoService.decrypt(encrypted);
    }

    private String normalizeDir(String dir) {
        String result = (dir == null || dir.isBlank()) ? DEFAULT_BACKUP_DIR : dir.trim();
        if (!result.startsWith("/")) {
            result = "/" + result;
        }
        while (result.length() > 1 && result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private void deleteRecursively(Path dir) {
        if (dir == null || !Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(this::deleteQuietly);
        } catch (IOException e) {
            log.warn("Не удалось удалить временную папку {}: {}", dir, e.getMessage());
        }
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.debug("Не удалось удалить {}: {}", path, e.getMessage());
        }
    }
}
