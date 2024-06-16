package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.ReviewsUserRepository;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.EmailService;

@Controller
public class Index {

    @Autowired
    private EmailService emailService;
    @Autowired
    private ReviewsUserRepository reviewsUserRepository;

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("reviewsLimit", reviewsUserRepository.findLimit());
        return "Index";
    }

    @PostMapping("/sendMail")
    public String  sendMail(@RequestBody EmailDetails details) {
        String status = emailService.sendSimpleMail(details);
        return status;
    }
}
