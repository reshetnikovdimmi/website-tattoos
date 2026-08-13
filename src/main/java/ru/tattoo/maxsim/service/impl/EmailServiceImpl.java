package ru.tattoo.maxsim.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.model.MailSettings;
import ru.tattoo.maxsim.service.interf.EmailService;
import ru.tattoo.maxsim.service.interf.MailSettingsService;

import java.util.Properties;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final MailSettingsService mailSettingsService;

    public EmailServiceImpl(MailSettingsService mailSettingsService) {
        this.mailSettingsService = mailSettingsService;
    }

    @Override
    public boolean sendSimpleMail(EmailDetails details) {
        try {
            MailSettings settings = mailSettingsService.getSettings();

            if (settings == null || !isValid(settings)) {
                log.error("Mail settings not found or invalid in database");
                return false;
            }

            JavaMailSenderImpl mailSender = createMailSender(settings);
            SimpleMailMessage mailMessage = createMailMessage(details, settings);
            mailSender.send(mailMessage);

            log.info("Email sent successfully from {} to {}",
                    settings.getFromEmail(), details.getRecipient());
            return true;

        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean isValid(MailSettings s) {
        return s.getHost() != null && !s.getHost().isBlank()
                && s.getUsername() != null && !s.getUsername().isBlank()
                && s.getPassword() != null && !s.getPassword().isBlank();
    }

    private JavaMailSenderImpl createMailSender(MailSettings settings) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(settings.getHost());
        sender.setPort(settings.getPort());
        sender.setUsername(settings.getUsername());
        sender.setPassword(settings.getPassword());

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", String.valueOf(settings.getAuth()));

        if ("SSL".equalsIgnoreCase(settings.getEncryption())) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.starttls.enable", "false");
        } else if ("TLS".equalsIgnoreCase(settings.getEncryption())) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.enable", "false");
        }

        props.put("mail.debug", String.valueOf(settings.getDebug()));

        // Таймауты — чтобы не зависнуть при сбое SMTP
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "5000");

        return sender;
    }

    private SimpleMailMessage createMailMessage(EmailDetails details, MailSettings settings) {
        SimpleMailMessage msg = new SimpleMailMessage();

        String to = (details.getRecipient() != null && !details.getRecipient().isBlank())
                ? details.getRecipient()
                : settings.getRecipientEmail();

        msg.setFrom(settings.getFromEmail());
        msg.setTo(to);
        msg.setSubject(formatSubject(details));
        msg.setText(formatMessageBody(details));

        return msg;
    }

    private String formatSubject(EmailDetails details) {
        return String.format("Контактная форма: %s - %s",
                details.getSubject(),
                details.getName());
    }

    private String formatMessageBody(EmailDetails details) {
        return String.format(
                "Новое сообщение с сайта:\n\n" +
                        "Имя: %s\n" +
                        "Контакт (телефон/email): %s\n" +
                        "Сообщение:\n%s\n\n" +
                        "---\n" +
                        "Отправлено автоматически с сайта",
                details.getName(),
                details.getSubject(),
                details.getMsgBody()
        );
    }
}