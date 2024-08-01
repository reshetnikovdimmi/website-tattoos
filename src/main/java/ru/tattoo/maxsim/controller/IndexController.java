package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.repository.ReviewsUserRepository;
import ru.tattoo.maxsim.service.interf.EmailService;

@Controller
public class IndexController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private ReviewsUserRepository reviewsUserRepository;


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("reviewsLimit", reviewsUserRepository.findLimit());
        model.addAttribute("details", new EmailDetails());
        return "Index";
    }

    @PostMapping("/sendMail")
    public String  sendMail(@ModelAttribute("details") EmailDetails details, Model model) {
        String status = emailService.sendSimpleMail(details);
        model.addAttribute("status", status);
        model.addAttribute("reviewsLimit", reviewsUserRepository.findLimit());
        model.addAttribute("details", new EmailDetails());
        return "Index";
    }
}
