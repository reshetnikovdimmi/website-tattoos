package ru.tattoo.maxsim.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.service.interf.PriceSectionService;


@Controller
@RequestMapping(PriceSectionController.URL)
public class PriceSectionController extends CRUDController<PriceSection, Long> {

    public static final String URL = "/price";
    public static final String PAGE_FRAGMENT = "fragment-admin";

    @Autowired
    private PriceSectionService priceSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    protected String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<PriceSection, Long> getService() {
        return priceSectionService;
    }



    @Override
    protected void updateSection(Model model) {
        model.addAttribute("price", new PriceSection());
        model.addAttribute("home", homeService.findAll());
    }

}
