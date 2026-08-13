package ru.tattoo.maxsim.exceptions.mail;

/**
 * Базовое исключение для всего mail-модуля.
 * Все специфичные исключения наследуются от него.
 */
public class MailException extends RuntimeException {

    private final String errorCode;

    public MailException(String message) {
        super(message);
        this.errorCode = "MAIL_ERROR";
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "MAIL_ERROR";
    }

    public MailException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
