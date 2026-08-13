package ru.tattoo.maxsim.exceptions.mail;

public class MailSettingsSaveException extends MailException {
    public MailSettingsSaveException(String message, Throwable cause) {
        super("MAIL_SAVE_ERROR", message, cause);
    }
}
