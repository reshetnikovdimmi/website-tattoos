package ru.tattoo.maxsim.exceptions.mail;

public class MailAuthException extends MailException {
    public MailAuthException(String message, Throwable cause) {
        super("MAIL_AUTH_ERROR", message, cause);
    }
}
