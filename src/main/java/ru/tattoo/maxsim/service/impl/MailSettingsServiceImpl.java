package ru.tattoo.maxsim.service.impl;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.tattoo.maxsim.exceptions.mail.MailSettingsSaveException;
import ru.tattoo.maxsim.exceptions.mail.MailValidationException;
import ru.tattoo.maxsim.model.DTO.SmtpTestResult;
import ru.tattoo.maxsim.model.MailSettings;
import ru.tattoo.maxsim.repository.MailSettingsRepository;
import ru.tattoo.maxsim.service.interf.MailSettingsService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSettingsServiceImpl implements MailSettingsService {

    private final MailSettingsRepository mailSettingsRepository;

    @Override
    public MailSettings getSettings() {
        return mailSettingsRepository.getDefaultOrCreate();
    }

    @Override
    @Transactional
    public MailSettings saveSettings(MailSettings settings) {
        log.debug("Сохранение настроек почты");

        // Только cross-field валидация (остальное — аннотации в контроллере)
        if (settings.getAuth() && settings.getId() == null && !StringUtils.hasText(settings.getPassword())) {
            throw new MailValidationException("При включённой аутентификации пароль обязателен");
        }

        try {
            MailSettings existing = mailSettingsRepository.findFirstByOrderByIdAsc()
                    .orElseGet(MailSettings::new);

            existing.setHost(settings.getHost());
            existing.setPort(settings.getPort());
            existing.setUsername(settings.getUsername());
            if (StringUtils.hasText(settings.getPassword())) {
                existing.setPassword(settings.getPassword());
            }
            existing.setFromEmail(settings.getFromEmail());
            existing.setFromName(settings.getFromName());
            existing.setRecipientEmail(settings.getRecipientEmail());
            existing.setEncryption(settings.getEncryption());
            existing.setAuth(settings.getAuth());
            existing.setDebug(settings.getDebug());
            existing.setSubjectTemplate(settings.getSubjectTemplate());
            existing.setAutoReply(settings.getAutoReply());

            MailSettings saved = mailSettingsRepository.save(existing);
            log.info("Настройки почты сохранены (id={})", saved.getId());
            return saved;

        } catch (Exception e) {
            log.error("Ошибка при сохранении настроек почты: {}", e.getMessage());
            throw new MailSettingsSaveException("Не удалось сохранить настройки почты", e);
        }
    }

    @Override
    @Transactional
    public MailSettings resetToDefault() {
        log.info("Сброс настроек почты к значениям по умолчанию");
        try {
            mailSettingsRepository.deleteAll();
            MailSettings def = new MailSettings();
            def.setHost("smtp.yandex.ru");
            def.setPort(465);
            def.setUsername("");
            def.setPassword("");
            def.setFromEmail("noreply@yourdomain.com");
            def.setFromName("Тату-студия Maxsim");
            def.setRecipientEmail("admin@yourdomain.com");
            def.setEncryption("SSL");
            def.setAuth(true);
            def.setDebug(false);
            def.setSubjectTemplate("Новое сообщение с сайта");
            def.setAutoReply(false);
            return mailSettingsRepository.save(def);
        } catch (Exception e) {
            throw new MailSettingsSaveException("Не удалось сбросить настройки", e);
        }
    }

    @Override
    public SmtpTestResult sendTestEmail(MailSettings settings, String testEmail) {
        ByteArrayOutputStream logStream = new ByteArrayOutputStream();
        PrintStream debugOut = new PrintStream(logStream, true, StandardCharsets.UTF_8);

        boolean success;
        String errorMessage = null;

        try {
            Session session = createDebugSession(settings, debugOut);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(settings.getFromEmail(), settings.getFromName()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(testEmail));
            message.setSubject("✅ Тестовое письмо от Тату-студии Maxsim");
            message.setText(
                    "Здравствуйте!\n\n" +
                            "Это тестовое письмо для проверки настроек SMTP.\n\n" +
                            "Если вы получили это письмо, значит настройки работают корректно!\n\n" +
                            "С уважением,\nТату-студия Maxsim",
                    StandardCharsets.UTF_8.name()
            );

            log.info("Sending test email to {} via {}:{}", testEmail, settings.getHost(), settings.getPort());
            Transport.send(message);

            success = true;
            log.info("Test email SENT successfully");

        } catch (AuthenticationFailedException e) {
            success = false;
            errorMessage = "Ошибка авторизации. Проверьте логин и пароль. Для Яндекса — пароль приложения.";
            log.error("Auth failed on send: {}", e.getMessage());

        } catch (MessagingException e) {
            success = false;
            errorMessage = diagnoseError(e);
            log.error("Send failed: {}", e.getMessage());
        } catch (Exception e) {
            success = false;
            errorMessage = "Ошибка при формировании письма: " + e.getMessage();
            log.error("Email build error: {}", e.getMessage());
        }

        return new SmtpTestResult(success, logStream.toString(StandardCharsets.UTF_8), errorMessage);
    }

    @Override
    public SmtpTestResult testConnection(MailSettings settings) {
        ByteArrayOutputStream logStream = new ByteArrayOutputStream();
        PrintStream debugOut = new PrintStream(logStream, true, StandardCharsets.UTF_8);

        boolean success;
        String errorMessage = null;

        try {
            Session session = createDebugSession(settings, debugOut);
            Transport transport = session.getTransport("smtp");

            log.info("Testing connection to {}:{}", settings.getHost(), settings.getPort());
            transport.connect(settings.getHost(), settings.getPort(),
                    settings.getUsername(), settings.getPassword());
            transport.close();

            success = true;
            log.info("Connection test PASSED");

        } catch (AuthenticationFailedException e) {
            success = false;
            errorMessage = "Ошибка авторизации. Проверьте логин и пароль.";
            log.error("Auth failed: {}", e.getMessage());

        } catch (MessagingException e) {
            success = false;
            errorMessage = diagnoseError(e);
            log.error("Connection failed: {}", e.getMessage());
        }

        return new SmtpTestResult(success, logStream.toString(StandardCharsets.UTF_8), errorMessage);
    }

    // ==================== PRIVATE ====================

    private Session createDebugSession(MailSettings settings, PrintStream debugOut) {
        Properties props = new Properties();
        props.put("mail.smtp.host", settings.getHost());
        props.put("mail.smtp.port", settings.getPort());
        props.put("mail.smtp.auth", settings.getAuth() ? "true" : "false");

        if ("SSL".equalsIgnoreCase(settings.getEncryption())) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", settings.getPort());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else if ("TLS".equalsIgnoreCase(settings.getEncryption())) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(settings.getUsername(), settings.getPassword());
            }
        });

        session.setDebug(true);
        session.setDebugOut(debugOut);

        return session;
    }

    private String diagnoseError(MessagingException e) {
        String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

        if (msg.contains("connection refused") || msg.contains("connect timed out")) {
            return "Не удалось подключиться к серверу. Проверьте хост и порт.";
        }
        if (msg.contains("unknown host")) {
            return "Неверный адрес SMTP-сервера.";
        }
        if (msg.contains("535") || msg.contains("5.7.8")) {
            return "Сервер отклонил авторизацию. Используйте пароль приложения.";
        }
        if (msg.contains("550") || msg.contains("5.7.1")) {
            return "Отправка заблокирована. Включите доступ для внешних приложений.";
        }
        if (msg.contains("tls") || msg.contains("ssl")) {
            return "Ошибка шифрования. Попробуйте сменить SSL/TLS или порт.";
        }

        return "Ошибка SMTP: " + e.getMessage();
    }
}