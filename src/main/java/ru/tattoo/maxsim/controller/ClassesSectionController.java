package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.AboutSection;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.text.ParseException;



@Controller
@RequestMapping(ClassesSectionController.URL)
public class ClassesSectionController extends CRUDController<ClassesSection, Long>{

    public static final String URL = "/classes";
    public static final String PAGE_FRAGMENT = "fragment-admin::classes";

    @Autowired
    private ClassesSectionService classesSectionService;
    @Autowired
    private HomeService homeService;

    @Override
    String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    CRUDService<ClassesSection, Long> getService() {
        return classesSectionService;
    }

    @Override
    void updateSection(Model model) {model.addAttribute("home", homeService.findAll());
        model.addAttribute("classes", new ClassesSection());
    }
}
