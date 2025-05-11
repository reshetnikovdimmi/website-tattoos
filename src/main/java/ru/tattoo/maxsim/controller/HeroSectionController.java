package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HeroSectionService;

@Controller
@RequestMapping("/hero")
public class HeroSectionController extends CRUDController<HomeHeroSection, Long>{

    @Autowired
    private HeroSectionService heroSectionService;



    @Override
    String getEntityName() {
        return  "admin::carousel-import";
    }

    @Override
    CRUDService<HomeHeroSection, Long> getService() {
        return heroSectionService;
    }
}
