package ru.tattoo.maxsim.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping(ContactController.URL)
public class ContactController extends CRUDController<ContactInfo, Long> {

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
        return "contact::contact-form";
    }

    @Override
    protected String getEntityName() {
        return "fragments::footer";
    }

    @Override
    protected CRUDService<ContactInfo, Long> getService() {
        return contactInfoService;
    }

    @Override
    protected void updateSection(Model model) {
        model.addAttribute("contactInfo", contactInfoRepository.findLimit());
        model.addAttribute("setting", settingWebsiteService.findAll());
    }
}
