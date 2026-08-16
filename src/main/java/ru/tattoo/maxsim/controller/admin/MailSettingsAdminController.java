package ru.tattoo.maxsim.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.tattoo.maxsim.model.DTO.SmtpTestResult;
import ru.tattoo.maxsim.model.MailSettings;
import ru.tattoo.maxsim.service.interf.MailSettingsService;

import java.io.UnsupportedEncodingException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/mail")
public class MailSettingsAdminController {

    private final MailSettingsService mailSettingsService;

    @GetMapping("/mail")
    public String mailSettings(Model model) {
        log.debug("Загрузка страницы настроек почты");

        if (!model.containsAttribute("mailSettings")) {
            model.addAttribute("mailSettings", mailSettingsService.getSettings());
        }
        model.addAttribute("testEmail", "");

        return "admin/settings/mail";
    }

    /**
     * Сохранение настроек с валидацией
     */
    @PostMapping("/save")
    public String saveMailSettings(
            @Valid @ModelAttribute("mailSettings") MailSettings settings,
            BindingResult bindingResult,
            Model model) {

        log.info("Сохранение настроек почты");

        // 1. Проверка аннотаций (@NotBlank, @Min, @Email...)
        if (bindingResult.hasErrors()) {
            model.addAttribute("mailSettings", settings);
            model.addAttribute("testEmail", "");
            return "fragment-admin::mailSettingsFragment";
        }

        // 2. Cross-field: если auth=true и это новая запись — пароль обязателен
        if (settings.getAuth() && settings.getId() == null && !StringUtils.hasText(settings.getPassword())) {
            bindingResult.rejectValue("password", "password.required",
                    "При включённой аутентификации пароль обязателен");
            model.addAttribute("mailSettings", settings);
            model.addAttribute("testEmail", "");
            return "fragment-admin::mailSettingsFragment";
        }

        // 3. Сохраняем
        mailSettingsService.saveSettings(settings);

        model.addAttribute("mailSettings", mailSettingsService.getSettings());
        model.addAttribute("testEmail", "");
        log.info("Настройки почты сохранены");

        return "fragment-admin::mailSettingsFragment";
    }

    /**
     * Быстрая проверка коннекта (без сохранения)
     */
    @PostMapping("/test-connection")

    public String testConnection(@ModelAttribute MailSettings settings,
                                 Model model) throws UnsupportedEncodingException {
        log.info("Quick SMTP connection test to {}", settings.getHost());

        restorePasswordIfEmpty(settings);

        SmtpTestResult result = mailSettingsService.testConnection(settings);

        // Если отладка выключена — не передаём логи в HTML
        if (!settings.getDebug()) {
            result = new SmtpTestResult(result.success(), "", result.errorMessage());
        }

        model.addAttribute("connectionResult", result);
        model.addAttribute("mailSettings", settings);

        return "fragment-admin::mailSettingsFragment";
    }

    /**
     * Отправка тестового письма (без сохранения)
     */
    @PostMapping("/test-send")

    public String testSend(@ModelAttribute MailSettings settings,
                           @RequestParam("testEmail") String testEmail,
                           Model model) throws UnsupportedEncodingException {

        log.info("Test email requested to: {}", testEmail);

        restorePasswordIfEmpty(settings);

        if (!StringUtils.hasText(testEmail)) {
            model.addAttribute("connectionResult",
                    new SmtpTestResult(false, "", "Укажите тестовый email"));
            model.addAttribute("mailSettings", settings);
            return "fragment-admin::mailSettingsFragment";
        }

        SmtpTestResult result = mailSettingsService.sendTestEmail(settings, testEmail.trim());

        model.addAttribute("connectionResult", result);
        model.addAttribute("mailSettings", settings);

        return "fragment-admin::mailSettingsFragment";
    }

    // ==================== PRIVATE ====================

    private void restorePasswordIfEmpty(MailSettings settings) {
        if (!StringUtils.hasText(settings.getPassword()) && settings.getId() != null) {
            MailSettings existing = mailSettingsService.getSettings();
            if (existing.getId().equals(settings.getId())) {
                settings.setPassword(existing.getPassword());
            }
        }
    }
}