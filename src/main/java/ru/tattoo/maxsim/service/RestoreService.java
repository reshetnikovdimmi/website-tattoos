package ru.tattoo.maxsim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.config.StorageProperties;
import ru.tattoo.maxsim.exceptions.BackupException;
import ru.tattoo.maxsim.model.BackupSettings;
import ru.tattoo.maxsim.model.DTO.BackupSet;
import ru.tattoo.maxsim.model.DTO.DiskItem;
import ru.tattoo.maxsim.util.ExecutableUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Восстановление из копии на Яндекс.Диске.
 *
 * <p><b>Обратный путь бэкапа.</b> Если {@link BackupService} делал дамп и складывал файлы
 * на Диск, то этот класс скачивает их обратно и разворачивает:
 * дамп заливается клиентом {@code mysql}, архив картинок распаковывается в каталог загрузок.
 *
 * <p><b>Чем отличается от бэкапа.</b> Бэкап только читает данные, а восстановление
 * <b>перезаписывает</b> текущую БД — операция деструктивная. Поэтому:
 * <ul>
 *   <li>она идёт только по явному подтверждению из админки;</li>
 *   <li>на время работы занимает тот же {@link BackupOperationLock}, что и бэкап;</li>
 *   <li>если что-то пойдёт не так, отката нет — нужен свежий бэкап перед восстановлением.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreService {

    private static final Pattern STAMP =
        Pattern.compile("^\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}$");
    private static final Pattern MYSQL_URL =
        Pattern.compile("^jdbc:mysql://([^:/?]+)(?::(\\d+))?/([^?;]+)");
    private static final String DEFAULT_BACKUP_DIR = "/tattoo-backups";

    private final BackupSettingsService settingsService;
    private final YandexDiskService yandexDiskService;
    private final StorageProperties storageProperties;
    private final CryptoService cryptoService;
    private final DataSourceProperties dataSourceProperties;
    private final BackupOperationLock operationLock;

    @Value("${app.backup.mysql-path:}")
    private String mysqlPath = "";

    @Value("${app.backup.restore-timeout-minutes:20}")
    private int restoreTimeoutMinutes = 20;

    public void restore(String stamp) {
        // stamp приходит из браузера, поэтому проверяем формат (защита от подстановки путей).
        if (stamp == null || !STAMP.matcher(stamp).matches()) {
            throw new BackupException("Некорректная дата копии: " + stamp);
        }
        if (!operationLock.tryLock()) {
            throw new BackupException(
                "Уже выполняется бэкап или восстановление — дождитесь завершения");
        }

        Path tempDir = null;
        try {
            // Шаг 1. Настройки, токен, папка на Диске.
            BackupSettings settings = settingsService.getOrCreate();
            String token = decryptToken(settings);
            String diskDir = normalizeDir(settings.getBackupDir());

            // Шаг 2. Находим нужную копию в плоском списке файлов (группировка — в BackupSet).
            BackupSet set = BackupSet.from(yandexDiskService.listFiles(token, diskDir)).stream()
                .filter(candidate -> stamp.equals(candidate.stamp()))
                .findFirst()
                .orElseThrow(() -> new BackupException(
                    "Копия " + stamp + " не найдена на Диске"));

            if (set.db() == null) {
                throw new BackupException(
                    "В копии " + stamp + " нет дампа БД — восстанавливать нечего");
            }

            // Шаг 3. Скачиваем дамп БД во временную папку и заливаем его в текущую базу.
            tempDir = Files.createTempDirectory("tattoo-restore-");
            Path dbArchive = tempDir.resolve("db.sql.gz");
            yandexDiskService.downloadFile(token, diskDir + "/" + set.db().getName(), dbArchive);
            restoreDatabase(dbArchive);

            // Шаг 4. Если в копии есть картинки — скачиваем и распаковываем их.
            if (set.images() != null) {
                Path imagesArchive = tempDir.resolve("images.tar.gz");
                yandexDiskService.downloadFile(token, diskDir + "/" + set.images().getName(), imagesArchive);
                restoreImages(imagesArchive);
            }

            log.info("Восстановление из копии {} завершено", stamp);

        } catch (IOException e) {
            throw new BackupException(
                "Ошибка ввода-вывода при восстановлении: " + e.getMessage(), e);
        } finally {
            operationLock.unlock();
            deleteRecursively(tempDir);
        }
    }

    /**
     * Заливает дамп в текущую БД через mysql-клиент.
     * Пароль передаётся через MYSQL_PWD, чтобы не светить в списке процессов.
     */
    void restoreDatabase(Path gzArchive) {
        String jdbcUrl = dataSourceProperties.getUrl();
        Matcher matcher = MYSQL_URL.matcher(jdbcUrl == null ? "" : jdbcUrl);
        if (!matcher.find()) {
            throw new BackupException("Не удалось разобрать spring.datasource.url: " + jdbcUrl);
        }

        String host = matcher.group(1);
        String port = matcher.group(2) != null ? matcher.group(2) : "3306";
        String database = matcher.group(3);
        String password = dataSourceProperties.getPassword();
        String binary = resolveMysql();

        // Сначала распаковываем .gz в обычный .sql: так клиенту mysql можно отдать файл
        // как stdin (redirectInput), не читая его в память Java-процесса.
        Path plainDump = gzArchive.resolveSibling("restore.sql");
        try (InputStream in = new GZIPInputStream(Files.newInputStream(gzArchive));
             OutputStream out = Files.newOutputStream(plainDump)) {
            in.transferTo(out);
        } catch (IOException e) {
            throw new BackupException("Не удалось распаковать дамп: " + e.getMessage(), e);
        }

        List<String> command = List.of(
            binary,
            "--host=" + host,
            "--port=" + port,
            "--user=" + dataSourceProperties.getUsername(),
            "--default-character-set=utf8mb4",
            database);

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectInput(plainDump.toFile());
        builder.redirectErrorStream(true);
        if (password != null && !password.isBlank()) {
            builder.environment().put("MYSQL_PWD", password);
        }

        try {
            Process process = builder.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            if (!process.waitFor(restoreTimeoutMinutes, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                throw new BackupException(
                    "mysql не завершился за " + restoreTimeoutMinutes + " мин");
            }
            if (process.exitValue() != 0) {
                throw new BackupException("mysql завершился с кодом " + process.exitValue()
                    + (output.isBlank() ? "" : ": " + output.trim()));
            }
        } catch (IOException e) {
            throw new BackupException("Не удалось запустить mysql (" + binary + "): "
                + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BackupException("Восстановление БД прервано", e);
        }
    }

    /**
     * Распаковывает images.tar.gz в каталог загрузок. Записи архива обязаны лежать
     * внутри images/, иначе — защита от path traversal.
     */
    void restoreImages(Path gzArchive) {
        Path uploadDir = Paths.get(storageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new BackupException("Не удалось создать каталог загрузок: " + e.getMessage(), e);
        }

        // Читаем архив как поток: tar -> gzip -> файл. Файлы копируем по одному,
        // поэтому даже большой архив не загружается в память целиком.
        try (InputStream in = new GZIPInputStream(Files.newInputStream(gzArchive));
             TarArchiveInputStream tar = new TarArchiveInputStream(in)) {

            TarArchiveEntry entry;
            while ((entry = tar.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (!name.startsWith("images/")) {
                    continue;
                }

                // Защита от path traversal: злоумышленник мог бы положить в архив
                // запись вида images/../../etc/passwd. normalize() + startsWith()
                // не дают выйти за пределы каталога загрузок.
                Path target = uploadDir.resolve(name.substring("images/".length())).normalize();
                if (!target.startsWith(uploadDir)) {
                    throw new BackupException("Недопустимый путь в архиве: " + name);
                }

                Files.createDirectories(target.getParent());
                Files.copy(tar, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new BackupException("Не удалось распаковать картинки: " + e.getMessage(), e);
        }
    }

    String resolveMysql() {
        try {
            return ExecutableUtils.resolve(mysqlPath, List.of(
                "mysql", "mariadb",
                "/usr/bin/mysql", "/usr/local/bin/mysql",
                "/usr/bin/mariadb", "/opt/homebrew/bin/mysql"),
                "Клиент mysql не найден. Установите mysql-client "
                    + "(в Docker-образе он есть) или задайте app.backup.mysql-path.");
        } catch (IllegalStateException e) {
            throw new BackupException(e.getMessage());
        }
    }

    private String decryptToken(BackupSettings settings) {
        String encrypted = settings.getYandexToken();
        if (encrypted == null || encrypted.isBlank()) {
            throw new BackupException("OAuth-токен Яндекс.Диска не задан");
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
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    log.debug("Не удалось удалить {}: {}", path, e.getMessage());
                }
            });
        } catch (IOException e) {
            log.warn("Не удалось удалить временную папку {}: {}", dir, e.getMessage());
        }
    }
}
