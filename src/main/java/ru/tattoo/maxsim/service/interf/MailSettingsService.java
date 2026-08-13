package ru.tattoo.maxsim.service.interf;

import ru.tattoo.maxsim.model.DTO.SmtpTestResult;
import ru.tattoo.maxsim.model.MailSettings;

public interface MailSettingsService {

    /**
     * Получить настройки почты
     */
    MailSettings getSettings();

    /**
     * Сохранить настройки почты
     */
    MailSettings saveSettings(MailSettings settings);

    /**
     * Сбросить настройки по умолчанию
     */
    MailSettings resetToDefault();

    /**
     * Отправить тестовое письмо
     */
    SmtpTestResult sendTestEmail(MailSettings settings, String testEmail);

    /**
     * Проверить подключение к SMTP
     */
    SmtpTestResult testConnection(MailSettings settings);
}