package ru.tattoo.maxsim.controller.admin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.tattoo.maxsim.model.*;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.service.interf.*;


@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final ImagesService imagesService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final SketchesService sketchesService;
    private final CommitsService commitsService;
    private final HomeService homeService;
    private final ContactInfoRepository contactInfoRepository;
    private final BlogService blogService;
    private final SettingWebsiteService settingWebsiteService;

    // Конструктор (вместо @Autowired на полях)
    public AdminController(
            ImagesService imagesService,
            ReviewService reviewService,
            UserService userService,
            SketchesService sketchesService,
            CommitsService commitsService,
            HomeService homeService,
            ContactInfoRepository contactInfoRepository,
            BlogService blogService,
            SettingWebsiteService settingWebsiteService) {
        this.imagesService = imagesService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.sketchesService = sketchesService;
        this.commitsService = commitsService;
        this.homeService = homeService;
        this.contactInfoRepository = contactInfoRepository;
        this.blogService = blogService;
        this.settingWebsiteService = settingWebsiteService;
    }

    @GetMapping
    public String showPage(Model model) {
        addCommonModelAttributes(model);
        return "admin";
    }

    @GetMapping("/home")
    public String showHomeFragment(Model model) {
        addCommonModelAttributes(model);
        return "fragment-admin::fragment-tab";
    }

    private void addCommonModelAttributes(Model model) {
        // Группируем по типу
        model.addAttribute("home", homeService.findAll());
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("sketches", sketchesService.findAll());
        model.addAttribute("commits", commitsService.findAll());
        model.addAttribute("gallery", imagesService.findAll());
        model.addAttribute("blog", blogService.findAll());
        model.addAttribute("setting", settingWebsiteService.findAll());
        model.addAttribute("contactInfo", contactInfoRepository.findAll().stream().findFirst().orElse(null));

        // Пустые сущности для форм
        model.addAttribute("sketchesEntity", new Sketches());
        model.addAttribute("hero", new HomeHeroSection());
        model.addAttribute("feature", new FeatureSection());
        model.addAttribute("about", new AboutSection());
        model.addAttribute("classes", new ClassesSection());
        model.addAttribute("chooseus", new ChooseusSection());
        model.addAttribute("images", new Images());
        model.addAttribute("blogEntity", new Blog());
        model.addAttribute("price", new PriceSection());
    }
}

