package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.service.interf.PriceSectionService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.text.ParseException;


@Controller
@RequestMapping(PriceSectionController.URL)
public class PriceSectionController extends CRUDController<PriceSection, Long>{

    public static final String URL = "/price";
    public static final String PAGE_FRAGMENT = "admin::price";

    @Autowired
    private PriceSectionService priceSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    CRUDService getService() {
        return priceSectionService;
    }

    @Override
    protected PriceSection prepareObject(PriceSection priceSection) throws IOException {
        priceSection.setSection("home");
        priceSection.setTitle("title");
        return priceSection;
    }

    @PostMapping("/import-prices")
    public String uploadPrice(@ModelAttribute("prices") PriceSection priceSection,
                         Model model) throws IOException, ParseException {
        priceSection.setSection("home");
        getService().create(priceSection);
        updateSection(model);
        return getEntityName();
    }


    @Override
    void updateSection(Model model) {
        model.addAttribute("home", homeService.findAll());
    }

}
