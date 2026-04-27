package ru.tattoo.maxsim.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.ChooseusSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ChooseusSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;

@Controller
@RequestMapping(ChooseusSectionController.URL)
public class ChooseusSectionController extends CRUDController<ChooseusSection, Long> {

    public static final String URL = "/chooseus";
    public static final String PAGE_FRAGMENT = "fragment-admin";

    @Autowired
    private ChooseusSectionService chooseusSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    protected String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<ChooseusSection, Long> getService() {
        return chooseusSectionService;
    }




    @Override
    protected void updateSection(Model model) {
        model.addAttribute("chooseus", new ChooseusSection());
        model.addAttribute("home", homeService.findAll());
    }
}
