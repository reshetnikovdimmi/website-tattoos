package ru.tattoo.maxsim.service;

import ru.tattoo.maxsim.model.EmailDetails;

public interface EmailService {

      String sendSimpleMail(EmailDetails details);

}