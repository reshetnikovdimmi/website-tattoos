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
import ru.tattoo.maxsim.model.DTO.BackupSet;
import ru.tattoo.maxsim.model.DTO.DiskItem;
import ru.tattoo.maxsim.util.ExecutableUtils;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

/**
 * Создание offsite-бэкапа: дамп БД + архив картинок + загрузка на Яндекс.Диск.
 *
 * <p><b>Как читать этот класс новичку.</b> Это «сердце» бэкапа. Его вызывает
 * {@link BackupWorker} (в отдельном потоке), а он сам последовательно делает:
 * <ol>
 *   <li>берёт настройки из БД ({@link BackupSettingsService});</li>
 *   <li>расшифровывает OAuth-токен Яндекс.Диска ({@link CryptoService});</li>
 *   <li>запускает внешнюю программу {@code mysqldump} и сжимает её вывод в gzip;</li>
 *   <li>упаковывает папку с картинками в tar.gz;</li>
 *   <li>загружает оба файла на Диск ({@link YandexDiskService});</li>
 *   <li>удаляет старые копии, оставляя последние {@code app.backup.keep};</li>
 *   <li>записывает в БД статус SUCCESS или FAILED.</li>
 * </ol>
 *
 * <p><b>Аннотации Spring.</b> {@code @Service} — «это бин, создай его один раз и передавай туда,
 * где попросят» (внедрение зависимостей). {@code @RequiredArgsConstructor} (Lombok) генерирует
 * конструктор для всех {@code final}-полей — именно через него Spring и передаёт зависимости.
 * {@code @Slf4j} (Lombok) добавляет поле {@code log} для логирования.
 *
 * <p>Класс не работает с HTTP: он ничего не знает про контроллеры и браузер. Это сознательно —
 * так его можно тестировать и вызывать из планировщика.
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

    // Зависимости (Spring подставит готовые объекты через конструктор).
    /** Настройки в БД: токен, папка, статус последнего прогона. */
    private final BackupSettingsService settingsService;
    /** Работа с API Яндекс.Диска: папки, загрузка, скачивание, удаление. */
    private final YandexDiskService yandexDiskService;
    /** Путь к папке с картинками (единый источник — app.storage.upload-dir). */
    private final StorageProperties storageProperties;
    /** AES-GCM: расшифровать токен, который лежит в БД в зашифрованном виде. */
    private final CryptoService cryptoService;
    /** Креды БД из spring.datasource.* — нужны, чтобы передать их mysqldump. */
    private final DataSourceProperties dataSourceProperties;
    /** Семафор: не даёт бэкапу и восстановлению работать одновременно. */
    private final BackupOperationLock operationLock;

    /** Путь к mysqldump. Пусто — искать в PATH (mysqldump / mariadb-dump). */
    @Value("${app.backup.mysqldump-path:}")
    private String mysqldumpPath = "";

    @Value("${app.backup.dump-timeout-minutes:15}")
    private int dumpTimeoutMinutes = 15;

    @Value("${app.backup.include-images:true}")
    private boolean includeImages = true;

    /** Сколько последних копий хранить на Диске (0 — не ротировать). */
    @Value("${app.backup.keep:3}")
    private int keepBackups = 3;

    /**
     * Делает бэкап и загружает файлы на Диск.
     *
     * <p>Почему здесь два метода (публичный {@code createBackup} и приватный {@code doCreateBackup})?
     * Чтобы блокировку было видно отдельно от бизнес-логики: {@code tryLock()} занимает «пропуск»,
     * а {@code finally} гарантированно его освобождает даже при исключении. Если бы {@code unlock()}
     * стоял не в {@code finally}, одна ошибка навсегда заблокировала бы все следующие бэкапы.
     *
     * @return имена загруженных файлов и их размеры (record {@link BackupResult})
     * @throws BackupException если дамп/архив не удался
     * @throws ru.tattoo.maxsim.exceptions.YandexDiskException если Диск недоступен или токен неверный
     */
    public BackupResult createBackup() {
        if (!operationLock.tryLock()) {
            throw new BackupException(
                "Уже выполняется бэкап или восстановление — дождитесь завершения");
        }
        try {
            return doCreateBackup();
        } finally {
            operationLock.unlock();
        }
    }

    private BackupResult doCreateBackup() {
        // tempDir объявлен снаружи try: он нужен в finally, чтобы удалить временные файлы
        // при любом исходе (успех, ошибка, исключение).
        Path tempDir = null;
        try {
            // Шаг 1. Настройки и токен.
            // getOrCreate() вернёт строку из таблицы backup_settings или создаст дефолтную.
            BackupSettings settings = settingsService.getOrCreate();
            String token = decryptToken(settings);

            // Шаг 2. Имена файлов. stamp — общий «штамп времени» для дампа и картинок,
            // чтобы их можно было сопоставить как одну копию.
            String stamp = LocalDateTime.now().format(STAMP);
            String diskDir = normalizeDir(settings.getBackupDir());
            String dbName = "db_" + stamp + ".sql.gz";
            String imagesName = "images_" + stamp + ".tar.gz";

            // Шаг 3. Временная папка на диске сервера. В ней лежат файлы, пока не уедут на Диск.
            // После завершения (finally) она удаляется, чтобы не забивать диск.
            tempDir = Files.createTempDirectory("tattoo-backup-");

            // Шаг 4. Дамп БД (внешняя программа mysqldump + gzip).
            Path dbFile = dumpDatabase(tempDir.resolve(dbName));

            // Шаг 5. Архив картинок. Если архив отключён в настройках — imagesFile == null.
            Path imagesFile = includeImages ? archiveImages(tempDir.resolve(imagesName)) : null;

            // Шаг 6. Загрузка на Яндекс.Диск: сначала убедиться, что папка существует,
            // затем двухшаговая загрузка каждого файла (см. YandexDiskService.uploadFile).
            yandexDiskService.createFolder(token, diskDir);
            yandexDiskService.uploadFile(token, dbFile, diskDir + "/" + dbName);
            if (imagesFile != null) {
                yandexDiskService.uploadFile(token, imagesFile, diskDir + "/" + imagesName);
            }

            // Шаг 7. Ротация: оставить только N последних копий.
            // Ошибки ротации не должны «ронять» уже успешный бэкап, поэтому метод
            // внутри сам ловит исключения и только пишет в лог.
            rotateRemoteBackups(token, diskDir);

            // Шаг 8. Отметить успех в БД и вернуть результат воркеру.
            settingsService.markBackupSuccess();

            long dbSize = Files.size(dbFile);
            long imagesSize = imagesFile == null ? 0 : Files.size(imagesFile);
            log.info("Бэкап загружен в {}: {}, {} ({} байт)", diskDir, dbName, imagesName,
                dbSize + imagesSize);

            // record BackupResult — неизменяемый «конверт» с результатом (аналог класса с геттерами).
            return new BackupResult(diskDir, dbName,
                imagesFile == null ? null : imagesName, dbSize, imagesSize);

        } catch (IOException e) {
            // IOException — проверяемое исключение (например, нет места на диске).
            // Помечаем прогон как FAILED и переводим в наше непроверяемое BackupException,
            // чтобы вызывающий код не обязан был объявлять throws.
            settingsService.markBackupFailed();
            throw new BackupException("Ошибка ввода-вывода при создании бэкапа: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            // Любая другая ошибка (нет токена, Диск недоступен, mysqldump упал).
            // Статус FAILED записываем и пробрасываем исключение дальше — его поймает BackupWorker.
            settingsService.markBackupFailed();
            throw e;
        } finally {
            // Выполняется всегда. Временные файлы не должны оставаться на сервере.
            deleteRecursively(tempDir);
        }
    }

    /**
     * Дамп БД в gzip через внешнюю программу {@code mysqldump}.
     *
     * <p>Java не умеет сама делать SQL-дамп, поэтому мы запускаем отдельный процесс
     * ({@link ProcessBuilder}) — тот же приём, что и запуск {@code git} или {@code ffmpeg} из кода.
     * Пароль не передаём аргументом (его видно в {@code ps}), а кладём в переменную окружения
     * {@code MYSQL_PWD} — это рекомендованный способ MySQL.
     */
    Path dumpDatabase(Path targetGz) {
        // spring.datasource.url выглядит так:
        // jdbc:mysql://db:3306/tattoo?useSSL=false
        // Регулярка вытаскивает три группы: host, port (может отсутствовать), database.
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

        // Сначала пишем несжатый dump в db.sql, в конце сожмём его в db.sql.gz.
        Path plainDump = targetGz.resolveSibling("db.sql");
        String dumpBinary = resolveMysqldump();

        // Список аргументов будущего процесса: программа + её флаги.
        // --single-transaction — консистентный снимок без блокировки таблиц;
        // --routines/--triggers — сохранить процедуры и триггеры;
        // --no-tablespaces — не требовать лишних прав у пользователя MySQL 8.
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
        // stdout процесса (то есть сам SQL-дамп) сразу пишем в файл.
        builder.redirectOutput(plainDump.toFile());
        // stderr оставляем отдельно — по нему поймём причину ошибки.
        builder.redirectErrorStream(false);
        if (password != null && !password.isBlank()) {
            builder.environment().put("MYSQL_PWD", password);
        }

        try {
            Process process = builder.start();
            // readAllBytes() на stderr блокируется, пока процесс не закроет поток (то есть завершится).
            String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

            // waitFor с таймаутом: если mysqldump «завис», убиваем его, а не ждём вечно.
            if (!process.waitFor(dumpTimeoutMinutes, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                throw new BackupException(
                    "mysqldump не завершился за " + dumpTimeoutMinutes + " мин");
            }
            // Ненулевой код возврата = программа завершилась с ошибкой.
            if (process.exitValue() != 0) {
                // Частая проблема MySQL 8: MariaDB-клиент не умеет caching_sha2_password.
                // Добавляем к ошибке человеческую подсказку.
                String hint = stderr.contains("caching_sha2_password")
                    ? " Клиент MySQL не поддерживает caching_sha2_password: нужен mysql-client-8.0"
                        + " (в Docker-образе установлен) или смена плагина пользователя БД."
                    : "";
                throw new BackupException("mysqldump завершился с кодом " + process.exitValue()
                    + (stderr.isBlank() ? "" : ": " + stderr.trim()) + hint);
            }
            // Пустой файл — тоже ошибка: значит, дамп не получился, хотя код возврата 0.
            if (Files.size(plainDump) == 0) {
                throw new BackupException("mysqldump вернул пустой дамп — проверьте доступ к БД");
            }

            // Сжимаем db.sql -> db.sql.gz потоково (без загрузки всего файла в память).
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
     * Архив папки с картинками в tar.gz.
     * Если папки нет — возвращает {@code null}: бэкап уйдёт без картинок (это не ошибка).
     *
     * <p><b>Как устроены потоки.</b> Файл → GZIP (сжатие) → TAR (структура архива) —
     * читать нужно с конца: данные пишутся в tar, тот сжимает их gzip-ом и кладёт в файл.
     * {@code try-with-resources} закроет все три потока автоматически в обратном порядке,
     * даже если внутри случится исключение.
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

            // Длинные имена файлов (кириллица, вложенные папки) — включаем POSIX-режим.
            tar.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

            // Files.walk возвращает Stream<Path> — ленивый обход всех файлов и подпапок.
            // Stream тоже ресурс, поэтому закрываем его в try-with-resources.
            try (Stream<Path> walk = Files.walk(imagesDir)) {
                for (Path file : walk.filter(Files::isRegularFile).toList()) {
                    // Внутри архива пути начинаются с images/, чтобы при восстановлении
                    // было понятно, куда их распаковывать.
                    String entryName = "images/" + imagesDir.relativize(file).toString()
                        .replace(File.separatorChar, '/');

                    // Одна запись архива: заголовок с именем/размером + содержимое файла.
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
        try {
            return ExecutableUtils.resolve(mysqldumpPath, List.of(
                "mysqldump", "mariadb-dump",
                "/usr/bin/mysqldump", "/usr/local/bin/mysqldump",
                "/usr/bin/mariadb-dump", "/opt/homebrew/bin/mysqldump"),
                "mysqldump не найден. Установите клиент MySQL (Debian/Ubuntu: "
                    + "sudo apt install mysql-client; macOS: brew install mysql-client) "
                    + "или задайте путь в app.backup.mysqldump-path. "
                    + "В Docker-образе клиент уже установлен.");
        } catch (IllegalStateException e) {
            throw new BackupException(e.getMessage());
        }
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

        List<BackupSet> sets = BackupSet.from(items);
        if (sets.isEmpty()) {
            return;
        }

        int keep = Math.max(keepBackups, 1);
        Set<String> keepStamps = new HashSet<>();
        for (int i = 0; i < Math.min(keep, sets.size()); i++) {
            keepStamps.add(sets.get(i).stamp());
        }

        // Гарантия: последняя полная (успешная) копия не удаляется,
        // даже если после неё были только неудачные попытки.
        sets.stream()
            .filter(BackupSet::isComplete)
            .findFirst()
            .ifPresent(set -> keepStamps.add(set.stamp()));

        for (BackupSet set : sets) {
            if (keepStamps.contains(set.stamp())) {
                continue;
            }
            deleteBackupFile(token, diskDir, set.db());
            deleteBackupFile(token, diskDir, set.images());
        }
    }

    private void deleteBackupFile(String token, String diskDir, DiskItem item) {
        if (item == null || item.getName() == null) {
            return;
        }
        try {
            yandexDiskService.delete(token, diskDir + "/" + item.getName());
            log.info("Ротация: удалён старый бэкап {}", item.getName());
        } catch (Exception e) {
            log.warn("Ротация: не удалось удалить {} — {}", item.getName(), e.getMessage());
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
