package ru.tattoo.maxsim.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @ModelAttribute
    public void addCommonAttributes (Model model){
        model.addAttribute("reviewsLimit" , reviewService.findLimit());
        model.addAttribute("carousel" , homeService.findAll());
    }

    @GetMapping("/")
    public String home(@ModelAttribute("details") EmailDetails details) {
        log.debug("Loading home page");
        return "Index";
    }

    @PostMapping("/mail")
    public String sendMail(
            @Valid @ModelAttribute("details") EmailDetails details,
            Model model,
            BindingResult bindingResult) {

        log.debug("Processing email request from: {}, subject: {}",
                details.getName(), details.getSubject());

        if (bindingResult.hasErrors()){
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
            // Валидация уже выполняется в JavaScript, но это резервная проверка
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "Index::map-contact-form";
        }

        try {
            boolean isSuccess = emailService.sendSimpleMail(details);
            model.addAttribute("status", isSuccess ?
                    "Сообщение отправлено" :
                    "Ошибка при отправке сообщения");
            log.info("Email sent successfully to: {}, from: {}",
                    details.getRecipient(), details.getName());
        } catch (Exception e){
            model.addAttribute("status", "Ошибка сервера. Попробуйте позже.");
            log.error("Error sending email: {}", e.getMessage());
        }

        model.addAttribute("details", new EmailDetails());
        return "Index::map-contact-form";
    }
}
