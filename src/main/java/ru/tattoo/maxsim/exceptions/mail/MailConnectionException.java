package ru.tattoo.maxsim.exceptions.mail;

public class MailConnectionException extends MailException {
    public MailConnectionException(String message, Throwable cause) {
        super("MAIL_CONN_ERROR", message, cause);
    }
}
