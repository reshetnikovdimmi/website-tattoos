package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.model.AboutSection;
import ru.tattoo.maxsim.service.interf.AboutSectionService;
import ru.tattoo.maxsim.service.interf.CRUDService;
@Controller
@RequestMapping("/about")
public class AboutSectionController extends CRUDController<AboutSection, Long> {

    @Autowired
    private AboutSectionService aboutSectionService;

    @Override
    String getEntityName() {
        return "admin::home-about";
    }

    @Override
    CRUDService<AboutSection, Long> getService() {
        return aboutSectionService;
    }
}
