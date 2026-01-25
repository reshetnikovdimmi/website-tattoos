package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.FeatureSection;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.FeatureSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;

@Controller
@RequestMapping(FeatureSectionController.URL)
public class FeatureSectionController extends CRUDController<FeatureSection, Long>{

    public static final String URL = "/feature";
    public static final String PAGE_FRAGMENT = "admin::feature-import";

    @Autowired
    private FeatureSectionService featureSectionService;

    @Autowired
    private HomeService homeService;

    @Override
    String getEntityName() {
        return PAGE_FRAGMENT;
    }

    @Override
    CRUDService<FeatureSection, Long> getService() {
        return featureSectionService;
    }


    @Override
    void updateSection(Model model) {
        model.addAttribute("home", homeService.findAll());
        model.addAttribute("feature", new FeatureSection());
    }


}
