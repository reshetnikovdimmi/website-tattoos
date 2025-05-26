package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;


@Controller
@RequestMapping("/classes")
public class ClassesSectionController extends CRUDController<ClassesSection, Long>{

    @Autowired
    private ClassesSectionService classesSectionService;

    @Override
    String getEntityName() {
        return "admin::classes-title";
    }

    @Override
    CRUDService<ClassesSection, Long> getService() {
        return classesSectionService;
    }
}
