package ru.tattoo.maxsim.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.*;
import ru.tattoo.maxsim.repository.SettingWebsiteRepository;
import ru.tattoo.maxsim.service.interf.*;

import java.io.IOException;
import java.text.ParseException;

@Controller
@Slf4j
@RequestMapping(SettingWebsiteController.URL)
public class SettingWebsiteController extends CRUDController<SettingWebsite, Long> {

    public static final String URL = "/admin/setting";

    @Autowired
    private SettingWebsiteService settingWebsiteService;
    @Autowired
    private UserService userService;
    @Autowired
    private ContactInfoService contactInfoService;
    @Autowired
    private MailSettingsService mailSettingsService;

    @GetMapping()
    private String getGalleryFragment(Model model, HttpServletRequest request) {
        log.info("Получено page {}",
                request.getRequestURL());

        updateSection(model);

        return getFragmentName()+"setting";
    }
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable("id") Long id,
                               Model model) throws IOException, ParseException {

        userService.deleteById(id);
        updateSection(model);

        return getFragmentName() + "user";
    }


    @PostMapping("/contact/import")
    public String createContact(@ModelAttribute() ContactInfo object,
                         Model model) throws IOException, ParseException {

        log.info("📞 СОХРАНЕНИЕ КОНТАКТОВ");
        log.info("   ID: {}", object.getId());
        log.info("   Телефон: {}", object.getTell());
        log.info("   Email: {}", object.getEmail());
        log.info("   Адрес: {}", object.getAddress());
        log.info("   Часы работы: {}", object.getWorkHours());

        contactInfoService.create(object);
        return "fragments::footer";
    }

    @PostMapping("/footer/import")
    public String createFooter(@ModelAttribute() SettingWebsite object,
                                Model model) throws IOException, ParseException {
        settingWebsiteService.create(object);
        return "fragments::footer";
    }


    @PostMapping("/logo-import")
    public String uploadLogo(@ModelAttribute() SettingWebsite object,
                              @RequestParam("file") MultipartFile fileImport,
                              @RequestParam(value = "fragment", required = false) String fragmentName,
                              Model model) throws IOException, ParseException {

        log.debug("Детали объекта до обработки: {}", object != null ? object.toString() : "null");

        getService().saveImg(fileImport, object);

        updateSection(model);

        return "fragments::" + fragmentName;
    }

    @PostMapping("/breadcrumb-import")
    public String uploadBreadcrumb(@ModelAttribute() SettingWebsite object,
                             @RequestParam("file") MultipartFile fileImport,
                             @RequestParam(value = "fragment", required = false) String fragmentName,
                             Model model) throws IOException, ParseException {

        log.debug("Детали объекта до обработки: {}", object != null ? object.toString() : "null");

        log.info("Фрагмент для обновления {}",
                fragmentName);

        getService().saveImg(fileImport, object);

        updateSection(model);

        return "admin::" + fragmentName;
    }

    String getEntityName(SettingWebsite object) {
        return getFragmentName() + object.getSection();
    }

    @Override
    protected String getFragmentName() {
        return "fragment-admin::";
    }

    @Override
    protected CRUDService<SettingWebsite, Long> getService() {
        return settingWebsiteService;
    }

    @Override
    protected void updateSection(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("setting", settingWebsiteService.findAll());
        model.addAttribute("mailSettings", mailSettingsService.getSettings());
    }
}
