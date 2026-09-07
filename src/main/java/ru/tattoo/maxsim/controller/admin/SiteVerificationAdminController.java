package ru.tattoo.maxsim.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tattoo.maxsim.model.SiteVerification;
import ru.tattoo.maxsim.service.interf.SiteVerificationService;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/verification")
public class SiteVerificationAdminController {

    private final SiteVerificationService verificationService;

    @GetMapping
    public String verificationPage(Model model) {
        model.addAttribute("verifications", verificationService.getAll());
        model.addAttribute("engines", SiteVerification.SearchEngine.values());
        model.addAttribute("methods", SiteVerification.VerificationMethod.values());
        model.addAttribute("newVerification", new SiteVerification());
        return "admin/settings/verification";
    }

    @PostMapping("/save")
    public String saveVerification(
            @RequestParam("engine") SiteVerification.SearchEngine engine,
            @RequestParam("method") SiteVerification.VerificationMethod method,
            @RequestParam("value") String value,
            @RequestParam(value = "fileContent", required = false) String fileContent,
            Model model) {

        log.info("Сохранение верификации: {} / {}", engine, method);

        SiteVerification verification = SiteVerification.builder()
                .engine(engine)
                .method(method)
                .value(value)
                .fileContent(fileContent)
                .active(true)
                .verified(false)
                .build();

        // Для HTML-файла генерируем содержимое автоматически
        if (method == SiteVerification.VerificationMethod.HTML_FILE) {
            String generated = verificationService.generateHtmlFileContent(value, engine.name());
            verification.setFileContent(generated);
        }

        verificationService.save(verification);
        model.addAttribute("verifications", verificationService.getAll());

        return "fragment-admin :: verificationFragment";
    }

    @GetMapping("/delete/{id}")
    public String deleteVerification(@PathVariable Long id, Model model) {
        verificationService.delete(id);
        model.addAttribute("verifications", verificationService.getAll());
        return "fragment-admin :: verificationFragment";
    }

    @PostMapping("/toggle/{id}")
    public String toggleVerification(@PathVariable Long id, Model model) {
        verificationService.findById(id).ifPresent(v -> {
            v.setActive(!Boolean.TRUE.equals(v.getActive()));
            verificationService.save(v);
        });
        model.addAttribute("verifications", verificationService.getAll());
        return "fragments/verification :: verificationFragment";
    }
}