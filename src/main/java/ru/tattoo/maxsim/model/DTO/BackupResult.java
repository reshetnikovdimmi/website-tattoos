package ru.tattoo.maxsim.model.DTO;

/**
 * Результат создания бэкапа: имена загруженных файлов и их размеры.
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
