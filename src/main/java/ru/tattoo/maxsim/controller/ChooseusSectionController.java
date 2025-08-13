package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ChooseusSection;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ChooseusSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.service.interf.PriceSectionService;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping("/chooseus")
public class ChooseusSectionController extends CRUDController {
    @Autowired
    private ChooseusSectionService chooseusSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    String getEntityName() {
        return "admin::chooseus-title";
    }

    @Override
    CRUDService getService() {
        return chooseusSectionService;
    }

    @Override
    protected Object prepareObject(MultipartFile fileImport, Object object) {
        return object;
    }

    @Override
    void updateSection(Model model) {

    }

    @PostMapping("/update-chooseus")

    public String uploadHome(@RequestBody ChooseusSection chooseusSection, Model model) {
        chooseusSection.setSection("home");
        chooseusSection.setTitle("title");
        getService().create(chooseusSection);
        model.addAttribute("home", homeService.findAll());
        return "admin::chooseus-title";
    }

    @PostMapping("/title")
    public String chooseus(@RequestParam("textH1") String textH1, @RequestParam("textH2") String textH2, @RequestParam("textH3") String textH3, @RequestParam("id") Long id, Model model) throws IOException, ParseException {
        ChooseusSection chooseusSection = new ChooseusSection();
        chooseusSection.setId(id);
        chooseusSection.setTextH2(textH1);
        chooseusSection.setTextH2(textH2);
        chooseusSection.setTextH3(textH3);
        chooseusSection.setSection("home");
        chooseusSection.setTitle("title");
        getService().create(chooseusSection);
        model.addAttribute("home", homeService.findAll());
        return "admin::chooseus-title";
    }
}
