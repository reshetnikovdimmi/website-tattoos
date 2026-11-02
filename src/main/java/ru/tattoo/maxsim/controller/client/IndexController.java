package ru.tattoo.maxsim.controller.client;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.service.interf.ContactInfoService;
import ru.tattoo.maxsim.service.interf.EmailService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.service.interf.ReviewService;

@Slf4j
@Controller
public class IndexController {

    private final EmailService emailService;
    private final ReviewService reviewService;
    private final HomeService homeService;
    private final ContactInfoService contactInfoService;

    public IndexController(EmailService emailService,
                           ReviewService reviewService,
                           HomeService homeService,
                           ContactInfoService contactInfoService) {
        this.emailService = emailService;
        this.reviewService = reviewService;
        this.homeService = homeService;
        this.contactInfoService = contactInfoService;
    }

    @ModelAttribute
    public void addCommonAttributes (Model model){
        model.addAttribute("reviewsLimit" , reviewService.findLimit());
        model.addAttribute("carousel" , homeService.findAll());
        model.addAttribute("contact" , contactInfoService.findAll());
    }

    @GetMapping("/")
    public String home(@ModelAttribute("details") EmailDetails details) {
        log.debug("Loading home page");
        return "Index";
    }

}
