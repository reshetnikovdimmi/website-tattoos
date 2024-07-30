package ru.tattoo.maxsim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.EmailDetails;

import java.io.File;

@Service
//todo: в пакете service можно создать подпакет impl и туда перенести имплементированные классы
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

      public String sendSimpleMail(EmailDetails details){

        try {

               SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(sender);
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject()+"-"+details.getName());

              javaMailSender.send(mailMessage);
          //todo: наверное правильнее будет возвращать true \ false, а в контроллерах переименовать переменную status в success \ isSuccess
            return "Mail Sent Successfully...";
        }

        catch (Exception e) {
          //todo: это лучше логировать и бросать кастомную ошибку
          return "Error while Sending Mail";
        }
    }
}
