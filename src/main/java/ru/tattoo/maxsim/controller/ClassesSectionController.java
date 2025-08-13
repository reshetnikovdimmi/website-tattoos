package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;

import java.io.IOException;
import java.text.ParseException;



@Controller
@RequestMapping("/classes")
public class ClassesSectionController extends CRUDController<ClassesSection, Long>{

    @Autowired
    private ClassesSectionService classesSectionService;
    @Autowired
    private HomeService homeService;

    @Override
    String getEntityName() {
        return "admin::classes";
    }

    @Override
    CRUDService<ClassesSection, Long> getService() {
        return classesSectionService;
    }

    @Override
    protected ClassesSection prepareObject(MultipartFile fileImport, ClassesSection object) {
        return object;
    }

    @Override
    void updateSection(Model model) {

    }

    @PostMapping("/title")
    public String classes(@RequestParam("textH2") String textH2,@RequestParam("textH3") String textH3, @RequestParam("id") Long id,Model model) throws IOException, ParseException {
        ClassesSection classesSection = new ClassesSection();
        classesSection.setId(id);
        classesSection.setTextH2(textH2);
        classesSection.setTextH3(textH3);
        classesSection.setSection("home");
        classesSection.setTitle("title");
        getService().create(classesSection);
        model.addAttribute("home", homeService.findAll());
        return "admin::classes-title";
    }
}
