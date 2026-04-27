package ru.tattoo.maxsim.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.FeatureSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.FeatureSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;

@Controller
@RequestMapping(FeatureSectionController.URL)
public class FeatureSectionController extends CRUDController<FeatureSection, Long> {

    public static final String URL = "/feature";
    public static final String PAGE_FRAGMENT = "fragment-admin";

    @Autowired
    private FeatureSectionService featureSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    protected String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<FeatureSection, Long> getService() {
        return featureSectionService;
    }


    @Override
    protected void updateSection(Model model) {
        model.addAttribute("home", homeService.findAll());
        model.addAttribute("feature", new FeatureSection());
    }

}
