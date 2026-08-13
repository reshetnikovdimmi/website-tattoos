package ru.tattoo.maxsim.exceptions;

public class FileUploadException extends RuntimeException {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public static FileUploadException emptyFile(String entityName) {
        return new FileUploadException("Файл не выбран для " + entityName);
    }

    public static FileUploadException invalidFormat(String fileName) {
        return new FileUploadException("Недопустимый формат файла: " + fileName +
                ". Поддерживаются: JPG, PNG, GIF, WEBP");
    }

    public static FileUploadException saveError(String fileName, Throwable cause) {
        return new FileUploadException("Ошибка при сохранении файла: " + fileName, cause);
    }
}
