package ru.tattoo.maxsim.exceptions.mail;

public class MailValidationException extends MailException {
    public MailValidationException(String message) {
        super("MAIL_VALIDATION_ERROR", message, null);
    }
}
