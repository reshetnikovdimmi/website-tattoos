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

/**
 * Глобальные атрибуты модели (футер) для контроллеров приложения.
 * Ограничено пакетом controller: иначе advice срабатывает и на /actuator/**,
 * дергает БД на каждый health-check и ломает проверку живости.
 */
@ControllerAdvice(basePackages = "ru.tattoo.maxsim.controller")
public class GlobalController {
    @Autowired
    private ContactInfoRepository contactInfoRepository;
    @Autowired
    private SettingWebsiteService settingWebsiteService;


    @ModelAttribute
    public void addGlobalData(Model model) {
        model.addAttribute("contactInfo", contactInfoRepository.findLimit());
        model.addAttribute("setting", settingWebsiteService.findAll());
    }

    @ModelAttribute("servletPath")
    public String getRequestServletPath(HttpServletRequest request) {
        return request.getServletPath();
    }
}