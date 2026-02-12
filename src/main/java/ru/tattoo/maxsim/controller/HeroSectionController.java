package ru.tattoo.maxsim.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HeroSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;

@Controller
@RequestMapping(HeroSectionController.URL)
public class HeroSectionController extends CRUDController<HomeHeroSection, Long>{

    public static final String URL = "/hero";
    public static final String PAGE_FRAGMENT = "fragment-admin::carousel-import";

    @Autowired
    private HeroSectionService heroSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    String getEntityName() {
        return  PAGE_FRAGMENT;
    }

    @Override
    CRUDService<HomeHeroSection, Long> getService() {
        return heroSectionService;
    }

    @Override
    void updateSection(Model model) {
        model.addAttribute("home", homeService.findAll());

        model.addAttribute("hero", new HomeHeroSection());
    }
}
