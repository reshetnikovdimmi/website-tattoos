package ru.tattoo.maxsim.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.repository.SettingWebsiteRepository;
import ru.tattoo.maxsim.service.interf.SettingWebsiteService;

@ControllerAdvice
public class GlobalController {
    @Autowired
    private ContactInfoRepository contactInfoRepository;
    @Autowired
    private SettingWebsiteService settingWebsiteService;


    @ModelAttribute("servletPath")
    String getRequestServletPath(HttpServletRequest request, Model model) {
        model.addAttribute("contactInfo", contactInfoRepository.findLimit());
        model.addAttribute("setting", settingWebsiteService.findAll());
             return request.getServletPath();
    }
}