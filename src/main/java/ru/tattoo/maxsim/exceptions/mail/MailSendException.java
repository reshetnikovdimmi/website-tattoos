package ru.tattoo.maxsim.exceptions.mail;

public class MailSendException extends MailException {
    public MailSendException(String message) {
        super("MAIL_SEND_ERROR", message, null);
    }
    public MailSendException(String message, Throwable cause) {
        super("MAIL_SEND_ERROR", message, cause);
    }
}
