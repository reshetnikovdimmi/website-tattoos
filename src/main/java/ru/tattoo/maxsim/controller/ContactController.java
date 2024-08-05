package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.service.interf.EmailService;

@Controller
public class ContactController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/contact")
    public String gallery(Model model) {
        model.addAttribute("details", new EmailDetails());
        return "contact";
    }

    @PostMapping("/send-mail")
    public String  sendMail(@ModelAttribute("details") EmailDetails details, Model model) {

        boolean isSuccess = emailService.sendSimpleMail(details);

           if(isSuccess){
               model.addAttribute("status", "Mail Sent Successfully...");
           }else{
               model.addAttribute("status", "Error while Sending Mail");
           }

        model.addAttribute("details", new EmailDetails());
        return "/contact";
    }




}
