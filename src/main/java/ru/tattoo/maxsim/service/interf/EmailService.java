package ru.tattoo.maxsim.service.interf;

import ru.tattoo.maxsim.model.EmailDetails;


public interface EmailService {

      boolean sendSimpleMail(EmailDetails details);

}