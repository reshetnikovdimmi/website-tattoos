package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Blog;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.model.SettingWebsite;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.SettingWebsiteService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping(SettingWebsiteController.URL)
public class SettingWebsiteController extends CRUDController<SettingWebsite, Long> {

    public static final String URL = "/setting";

    @Autowired
    private SettingWebsiteService settingWebsiteService;


    @PostMapping("/head/{section}")
    public String uploadHome(@ModelAttribute()SettingWebsite settingWebsite, @PathVariable("section") String section, Model model) {
        settingWebsite.setSection(section);
        settingWebsiteService.create(settingWebsite);
        model.addAttribute("setting", settingWebsiteService.findAll());
        return "admin::"+section;
    }
    @Override
    @PostMapping("/import")
    public String createEntity(@ModelAttribute("hero") SettingWebsite object,
                         Model model) throws IOException, ParseException {
        getService().create(object);
        model.addAttribute("setting", settingWebsiteService.findAll());
        return "fragments::footer";
    }

    @PostMapping("/image-import")
    public String uploadImage(@ModelAttribute("hero") SettingWebsite object,
                              @RequestParam("file") MultipartFile fileImport,
                              Model model) throws IOException, ParseException {

        object = getService().findById(object.getId());

        getService().saveImg(fileImport, object);

        updateSection(model);
        return getEntityName(object);
    }

    String getEntityName(SettingWebsite object) {
        return getEntityName() + object.getSection();
    }

    @Override
    String getEntityName() {
        return "admin::";
    }

    @Override
    CRUDService<SettingWebsite, Long> getService() {
        return settingWebsiteService;
    }

    @Override
    void updateSection(Model model) {
        model.addAttribute("setting", settingWebsiteService.findAll());
    }
}
