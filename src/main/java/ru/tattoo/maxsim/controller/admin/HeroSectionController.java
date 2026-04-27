package ru.tattoo.maxsim.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HeroSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;

@Controller
@RequestMapping(HeroSectionController.URL)
public class HeroSectionController extends CRUDController<HomeHeroSection, Long> {

    public static final String URL = "/hero";
    public static final String PAGE_FRAGMENT = "fragment-admin";

    @Autowired
    private HeroSectionService heroSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    protected String getEntityName() {
        return  PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<HomeHeroSection, Long> getService() {
        return heroSectionService;
    }

    @Override
    protected void updateSection(Model model) {

        model.addAttribute("home", homeService.findAll());
        model.addAttribute("hero", new HomeHeroSection());

    }
}
