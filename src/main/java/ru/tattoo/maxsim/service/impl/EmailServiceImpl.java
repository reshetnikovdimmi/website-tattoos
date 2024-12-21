package ru.tattoo.maxsim.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.service.interf.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public boolean sendSimpleMail(EmailDetails details) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(sender);
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject() + "-" + details.getName());

            javaMailSender.send(mailMessage);

            return true;

        } catch (Exception e) {
            LOG.error("Error while sending out email..{}", e.getMessage());
            return false;
        }
    }
}
