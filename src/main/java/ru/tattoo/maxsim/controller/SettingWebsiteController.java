package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.*;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.ContactInfoService;
import ru.tattoo.maxsim.service.interf.SettingWebsiteService;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.util.ImageUtils;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.text.ParseException;

@Controller
@Slf4j
@RequestMapping(SettingWebsiteController.URL)
public class SettingWebsiteController extends CRUDController<SettingWebsite, Long> {

    public static final String URL = "/setting";

    @Autowired
    private SettingWebsiteService settingWebsiteService;
    @Autowired
    private UserService userService;
    @Autowired
    private ContactInfoService contactInfoService;

    @GetMapping("/admin")
    private String getGalleryFragment(Model model, HttpServletRequest request) {
        log.info("Получено page {}",
                request.getRequestURL());
        model.addAttribute("setting", settingWebsiteService.findAll());
        model.addAttribute("users", userService.findAll());

        return "fragment-admin::setting";
    }

    @PostMapping("/head/{section}")
    public String uploadHome(@ModelAttribute()SettingWebsite settingWebsite, @PathVariable("section") String section, Model model) {
        settingWebsite.setSection(section);
        settingWebsiteService.create(settingWebsite);
        model.addAttribute("setting", settingWebsiteService.findAll());
        return "Извлечь метод...::"+section;
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
        return "fragment-admin::";
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
