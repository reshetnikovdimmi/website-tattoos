package ru.tattoo.maxsim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.EmailDetails;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.model.SettingWebsite;
import ru.tattoo.maxsim.service.interf.SettingWebsiteService;

import java.io.IOException;
import java.text.ParseException;

@Controller
public class SettingWebsiteController {

    @Autowired
    private SettingWebsiteService settingWebsiteService;

    @GetMapping("/breadcrumb")
    public String gallery(Model model) {
        model.addAttribute("details", new EmailDetails());
        return "breadcrumb";
    }

    @PostMapping("/sends")
    public String  sendMail(@ModelAttribute("details") EmailDetails details, Model model) {

        return "/contact";
    }

    @PostMapping("/setting/upload-image/{section}")
    public String uploadHome(@RequestParam("image") MultipartFile fileImport, @RequestParam("id") Long id,
                             @PathVariable("section") String section,
                             Model model) throws IOException, ParseException {

        settingWebsiteService.saveImg(fileImport, section, id);
        model.addAttribute("setting", settingWebsiteService.findAll());
        return "admin::" + section;
    }
    @PostMapping("/setting/head/{section}")
    public String uploadHome(@RequestBody SettingWebsite settingWebsite, @PathVariable("section") String section, Model model) {
        settingWebsite.setSection(section);
        settingWebsiteService.create(settingWebsite);
        model.addAttribute("setting", settingWebsiteService.findAll());
        return "admin::"+section;
    }
}
