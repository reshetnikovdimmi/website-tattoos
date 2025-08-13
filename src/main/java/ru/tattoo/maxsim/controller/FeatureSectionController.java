package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.FeatureSection;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.FeatureSectionService;

@Controller
@RequestMapping("/feature")
public class FeatureSectionController extends CRUDController<FeatureSection, Long>{

    @Autowired
    private FeatureSectionService featureSectionService;

    @Override
    String getEntityName() {
        return "admin::feature-import";
    }

    @Override
    CRUDService<FeatureSection, Long> getService() {
        return featureSectionService;
    }

    @Override
    protected FeatureSection prepareObject(MultipartFile fileImport, FeatureSection object) {
        return object;
    }

    @Override
    void updateSection(Model model) {

    }


}
