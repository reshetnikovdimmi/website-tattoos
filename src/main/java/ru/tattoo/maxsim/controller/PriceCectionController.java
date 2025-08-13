package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.service.interf.PriceSectionService;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


@Controller
@RequestMapping("/price")
public class PriceCectionController extends CRUDController{

    @Autowired
    private PriceSectionService priceSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    String getEntityName() {
        return "";
    }

    @Override
    CRUDService getService() {
        return priceSectionService;
    }

    @Override
    protected Object prepareObject(MultipartFile fileImport, Object object) {
        return object;
    }

    @Override
    void updateSection(Model model) {

    }

    @PostMapping("/update-prices")

    public String uploadHome(@RequestBody PriceSection priceSection, Model model) {
        priceSection.setSection("home");
        getService().create(priceSection);
        model.addAttribute("home", homeService.findAll());
        return "admin::price-title";
    }

    @PostMapping("/title")
    public String classes(@RequestParam("textH2") String textH2, @RequestParam("textH3") String textH3, @RequestParam("id") Long id, Model model) throws IOException, ParseException {
        PriceSection priceSection = new PriceSection();
        priceSection.setId(id);
        priceSection.setTextH2(textH2);
        priceSection.setTextH3(textH3);
        priceSection.setSection("home");
        priceSection.setTitle("title");
        getService().create(priceSection);
        model.addAttribute("home", homeService.findAll());
        return "admin::price-title";
    }
}
