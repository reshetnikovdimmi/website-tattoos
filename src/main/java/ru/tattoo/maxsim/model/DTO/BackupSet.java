package ru.tattoo.maxsim.model.DTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Одна резервная копия (один прогон): пара файлов
 * {@code db_<дата>.sql.gz} + {@code images_<дата>.tar.gz}.
 *
 * <p><b>Зачем этот класс.</b> Яндекс.Диск возвращает <em>плоский</em> список файлов.
 * Шаблону неудобно (и опасно) склеивать их в пары — это делает Java, а шаблон получает
 * уже готовый список объектов с полями {@code db}, {@code images}, {@code dateLabel}.
 *
 * <p>{@code record} — современный способ объявить неизменяемый класс данных:
 * компилятор сам создаёт конструктор, методы {@code db()}, {@code images()} и т.д.
 * (вместо геттеров {@code getDb()}). Именно поэтому в шаблоне пишется
 * {@code set.dateLabel}, а не {@code set.getDateLabel()}.
 *
 * <p>Статический фабричный метод {@link #from(List)} — обычная практика: он превращает
 * «сырые» данные API в удобные объекты и заодно прячет всю логику группировки.
 */
public record BackupSet(
    String stamp,
    String dateLabel,
    DiskItem db,
    DiskItem images,
    long totalSize
) {

    private static final Pattern FILE_NAME = Pattern.compile(
        "^(db|images)_(\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2})\\.(?:sql|tar)\\.gz$");
    private static final DateTimeFormatter STAMP =
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter LABEL =
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Группирует файлы по дате прогона, новые — первыми.
     * Посторонние файлы и папки игнорируются.
     */
    public static List<BackupSet> from(List<DiskItem> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        Map<String, DiskItem> dbFiles = new HashMap<>();
        Map<String, DiskItem> imageFiles = new HashMap<>();

        for (DiskItem item : items) {
            if (item == null || item.getName() == null || "dir".equals(item.getType())) {
                continue;
            }
            Matcher matcher = FILE_NAME.matcher(item.getName());
            if (!matcher.matches()) {
                continue;
            }
            String stamp = matcher.group(2);
            if ("db".equals(matcher.group(1))) {
                dbFiles.putIfAbsent(stamp, item);
            } else {
                imageFiles.putIfAbsent(stamp, item);
            }
        }

        return Stream.concat(dbFiles.keySet().stream(), imageFiles.keySet().stream())
            .distinct()
            .sorted(Comparator.reverseOrder())
            .map(stamp -> new BackupSet(
                stamp,
                label(stamp),
                dbFiles.get(stamp),
                imageFiles.get(stamp),
                size(dbFiles.get(stamp)) + size(imageFiles.get(stamp))))
            .toList();
    }

    public boolean hasDb() {
        return db != null;
    }

    public boolean hasImages() {
        return images != null;
    }

    /** Полная копия: есть и дамп БД, и архив картинок. */
    public boolean isComplete() {
        return db != null && images != null;
    }

    private static String label(String stamp) {
        try {
            return LocalDateTime.parse(stamp, STAMP).format(LABEL);
        } catch (RuntimeException e) {
            return stamp;
        }
    }

    private static long size(DiskItem item) {
        return item != null && item.getSize() != null ? item.getSize() : 0L;
    }
}
