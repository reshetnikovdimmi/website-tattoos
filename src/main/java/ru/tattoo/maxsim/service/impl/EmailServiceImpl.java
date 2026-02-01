package ru.tattoo.maxsim.service.impl;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.service.interf.EmailService;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final String senderAddress;
    private final String defaultRecipient;

    public EmailServiceImpl(JavaMailSender javaMailSender,
                            @Value("${spring.mail.username}") String senderAddress,
                            @Value("${app.email.default-recipient:}") String defaultRecipient) {
        this.javaMailSender = javaMailSender;
        this.senderAddress = senderAddress;
        // Если defaultRecipient не указан в конфиге, используем senderAddress
        this.defaultRecipient = defaultRecipient.isEmpty() ? senderAddress : defaultRecipient;
    }

    @Override
    public boolean sendSimpleMail(EmailDetails details) {
        try {
            SimpleMailMessage mailMessage = createMailMessage(details);
            javaMailSender.send(mailMessage);
            log.debug("Email sent successfully: {}", mailMessage.getSubject());
            return true;
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            return false;
        }
    }

    private SimpleMailMessage createMailMessage(EmailDetails details) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        // Используем recipient из details или дефолтный
        String recipient = (details.getRecipient() != null && !details.getRecipient().isEmpty()) ?
                details.getRecipient() : defaultRecipient;

        mailMessage.setFrom(senderAddress);
        mailMessage.setTo(recipient);
        mailMessage.setSubject(formatSubject(details));
        mailMessage.setText(formatMessageBody(details));

        return mailMessage;
    }

    private String formatSubject(EmailDetails details) {
        // Форматируем тему: телефон + имя
        return String.format("Контактная форма: %s - %s",
                details.getSubject(),
                details.getName());
    }

    private String formatMessageBody(EmailDetails details) {
        // Форматируем тело письма для лучшей читаемости
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
