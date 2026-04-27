package ru.tattoo.maxsim.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;


@Controller
@RequestMapping(ClassesSectionController.URL)
public class ClassesSectionController extends CRUDController<ClassesSection, Long> {

    public static final String URL = "/classes";
    public static final String PAGE_FRAGMENT = "fragment-admin";

    @Autowired
    private ClassesSectionService classesSectionService;
    @Autowired
    private HomeService homeService;

    @Override
    protected String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<ClassesSection, Long> getService() {
        return classesSectionService;
    }

    @Override
    protected void updateSection(Model model) {model.addAttribute("home", homeService.findAll());
        model.addAttribute("classes", new ClassesSection());
    }
}
