package ru.tattoo.maxsim.model.DTO;

/**
 * Результат успешного бэкапа: что именно загрузили и сколько это весит.
 *
 * <p>Это {@code record} — неизменяемый «конверт» с данными. Он нужен, чтобы
 * {@link ru.tattoo.maxsim.service.BackupService} мог вернуть сразу несколько значений
 * (в Java метод возвращает только одно значение, поэтому их упаковывают в объект).
 *
 * <p>{@code imagesFile == null} означает, что архив картинок не создавался
 * (отключён в настройках или каталога нет).
 */
public record BackupResult(
    String diskDir,
    String dbFile,
    String imagesFile,
    long dbSize,
    long imagesSize
) {
    public long totalSize() {
        return dbSize + imagesSize;
    }
}
