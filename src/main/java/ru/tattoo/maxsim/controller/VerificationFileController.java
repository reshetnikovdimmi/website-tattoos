package ru.tattoo.maxsim.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.tattoo.maxsim.model.SiteVerification;
import ru.tattoo.maxsim.service.interf.SiteVerificationService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Публичный контроллер для отдачи файлов верификации.
 * Яндекс и Google запрашивают файл по прямому URL.
 */
@Controller
@RequiredArgsConstructor
public class VerificationFileController {

    private final SiteVerificationService verificationService;

    /**
     * Отдает HTML-файл верификации Яндекса.
     * URL: /yandex_{verificationCode}.html
     */
    @GetMapping(value = "/yandex_{code}.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> yandexVerificationFile(@PathVariable String code) {
        Optional<SiteVerification> verification = verificationService
                .getByEngine(SiteVerification.SearchEngine.YANDEX).stream()
                .filter(v -> v.getMethod() == SiteVerification.VerificationMethod.HTML_FILE)
                .filter(v -> Boolean.TRUE.equals(v.getActive()))
                .filter(v -> v.getValue() != null && v.getValue().contains(code))
                .findFirst();

        if (verification.isPresent() && verification.get().getFileContent() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(verification.get().getFileContent());
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Отдает HTML-файл верификации Google.
     * URL: /google{verificationCode}.html
     */
    @GetMapping(value = "/google{code}.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> googleVerificationFile(@PathVariable String code) {
        Optional<SiteVerification> verification = verificationService
                .getByEngine(SiteVerification.SearchEngine.GOOGLE).stream()
                .filter(v -> v.getMethod() == SiteVerification.VerificationMethod.HTML_FILE)
                .filter(v -> Boolean.TRUE.equals(v.getActive()))
                .filter(v -> v.getValue() != null && v.getValue().contains(code))
                .findFirst();

        if (verification.isPresent() && verification.get().getFileContent() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(verification.get().getFileContent());
        }

        return ResponseEntity.notFound().build();
    }
}