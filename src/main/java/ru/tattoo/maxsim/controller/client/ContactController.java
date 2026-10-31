package ru.tattoo.maxsim.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ContactInfoService;
import ru.tattoo.maxsim.service.interf.EmailService;
import ru.tattoo.maxsim.service.interf.SettingWebsiteService;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping(ContactController.URL)
public class ContactController{

    public static final String URL = "/contact";

    @Autowired
    private EmailService emailService;

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @Autowired
    private ContactInfoService contactInfoService;

    @Autowired
    private SettingWebsiteService settingWebsiteService;

    @GetMapping()
    public String contact(Model model) {
        model.addAttribute("details", new EmailDetails());
        model.addAttribute("contact" , contactInfoService.findAll());
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
        return "Index::contact-form";
    }
}
