package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.AboutSection;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.service.interf.AboutSectionService;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;

@Controller
@RequestMapping(AboutSectionController.URL)
public class AboutSectionController extends CRUDController<AboutSection, Long> {

    public static final String URL = "/about";
    public static final String PAGE_FRAGMENT = "admin::home-about";

    @Autowired
    private AboutSectionService aboutSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    CRUDService<AboutSection, Long> getService() {
        return aboutSectionService;
    }



    @Override
    void updateSection(Model model) {
        model.addAttribute("home", homeService.findAll());
        model.addAttribute("about", new AboutSection());
    }
}
