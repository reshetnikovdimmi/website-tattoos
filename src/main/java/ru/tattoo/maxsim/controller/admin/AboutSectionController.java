package ru.tattoo.maxsim.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.AboutSection;
import ru.tattoo.maxsim.service.interf.AboutSectionService;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HomeService;

@Controller
@RequestMapping(AboutSectionController.URL)
public class AboutSectionController extends CRUDController<AboutSection, Long> {

    public static final String URL = "/about";
    public static final String PAGE_FRAGMENT = "fragment-admin";

    @Autowired
    private AboutSectionService aboutSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    protected String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<AboutSection, Long> getService() {
        return aboutSectionService;
    }



    @Override
    protected void updateSection(Model model) {
        model.addAttribute("home", homeService.findAll());
        model.addAttribute("about", new AboutSection());
    }
}
