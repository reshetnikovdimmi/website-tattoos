package ru.tattoo.maxsim.exceptions;

public class YandexDiskException extends RuntimeException {
    public YandexDiskException(String message) {
        super(message);
    }
    public YandexDiskException(String message, Throwable cause) {
        super(message, cause);
    }
}
