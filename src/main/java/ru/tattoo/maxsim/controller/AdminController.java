package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.tattoo.maxsim.model.*;
import ru.tattoo.maxsim.model.DTO.CommitsDTO;
import ru.tattoo.maxsim.model.DTO.GalleryDTO;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.service.interf.*;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.text.ParseException;
import java.util.stream.Collectors;


@Controller
@Slf4j
@RequestMapping(AdminController.ADMIN_URL)
public class AdminController {

    private static final String ALL_GALLERY = "Вся галерея";
    private static final int PAGE_NUMBER = 0;
    public static final String ADMIN_URL = "/admin";
    public static final String ADMIN_NAME = "admin";


    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private SketchesService sketchesService;

    @Autowired
    private CommitsService commitsService;

    @Autowired
    private HomeService homeService;

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BlogService blogService;

    @Autowired
    private SettingWebsiteService settingWebsiteService;



    @GetMapping
    public String showPage(Model model) {
        populateAdminDashboard(model);
        return getEntityName();
    }

    @GetMapping("/home")
    public String showHomeFragment(Model model, HttpServletRequest request) {
        log.info("Получено page {}",
                request.getRequestURL());
        populateAdminDashboard(model);
        return "fragment-admin::fragment-tab";
    }


    // Вспомогательные методы для уменьшения дублирования кода
    private void populateAdminDashboard(Model model) {

        model.addAttribute("sketchesEntity", new Sketches());
        model.addAttribute("hero", new HomeHeroSection());
        model.addAttribute("feature", new FeatureSection());
        model.addAttribute("about", new AboutSection());
        model.addAttribute("classes", new ClassesSection());
        model.addAttribute("chooseus", new ChooseusSection());
        model.addAttribute("images", new Images());
        model.addAttribute("blogEntity", new Blog());
       /* String normalizedStyle =  null;
        model.addAttribute("gallery", imagesService.getGalleryDto(normalizedStyle, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("interestingWorks", blogService.findDescription());
        model.addAttribute("blog", blogService.findAll());
        model.addAttribute("setting", settingWebsiteService.findAll());
        model.addAttribute("commits", commitsService.findAll().stream()
                .map(commits -> modelMapper.map(commits, CommitsDTO.class)).collect(Collectors.toList()));*/
        model.addAttribute("home", homeService.findAll());

    }


    String getEntityName() {
        return ADMIN_NAME;
    }

}

