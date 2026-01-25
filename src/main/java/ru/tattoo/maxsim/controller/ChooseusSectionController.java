package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ChooseusSection;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ChooseusSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.service.interf.PriceSectionService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping(ChooseusSectionController.URL)
public class ChooseusSectionController extends CRUDController<ChooseusSection, Long> {

    public static final String URL = "/chooseus";
    public static final String PAGE_FRAGMENT = "admin::chooseus";

    @Autowired
    private ChooseusSectionService chooseusSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    CRUDService<ChooseusSection, Long> getService() {
        return chooseusSectionService;
    }




    @Override
    void updateSection(Model model) {
        model.addAttribute("chooseus", new ChooseusSection());
        model.addAttribute("home", homeService.findAll());
    }
}
