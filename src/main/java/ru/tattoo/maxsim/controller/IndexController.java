package ru.tattoo.maxsim.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.service.interf.EmailService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.service.interf.ReviewService;

@Slf4j
@Controller
public class IndexController {

    private final EmailService emailService;
    private final ReviewService reviewService;
    private final HomeService homeService;

    public IndexController(EmailService emailService,
                           ReviewService reviewService,
                           HomeService homeService) {
        this.emailService = emailService;
        this.reviewService = reviewService;
        this.homeService = homeService;
    }

    @GetMapping("/")
    public String home(Model model) {
        prepareModel(model);
        return "Index";
    }

    @PostMapping("/mail")
    public String sendMail(@ModelAttribute("details") EmailDetails details, Model model, BindingResult bindingResult) {

        if (bindingResult.hasErrors()){
            return "Index::map-contact-form";
        }

        try {
            boolean isSuccess = emailService.sendSimpleMail(details);
            model.addAttribute("status", isSuccess ?
                    "Сообщение отправлено" :
                    "Ошибка при отправке сообщения");
        } catch (Exception e){
            model.addAttribute("status", "Ошибка сервера. Попробуйте позже.");
            log.error("Error sending email: {}", e.getMessage());
        }

        model.addAttribute("details", new EmailDetails());
        return "Index::map-contact-form";
    }

    private void prepareModel(Model model) {
        model.addAttribute("reviewsLimit", reviewService.findLimit());
        model.addAttribute("details", new EmailDetails());
        model.addAttribute("carousel", homeService.findAll());
    }
}
