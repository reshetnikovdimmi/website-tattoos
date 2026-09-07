package ru.tattoo.maxsim.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.tattoo.maxsim.model.SiteVerification;
import ru.tattoo.maxsim.service.interf.SiteVerificationService;

/**
 * Добавляет мета-теги верификации во все модели для Thymeleaf.
 * Вставьте в <head> шаблона: <th:block th:replace="~{fragments/head-verification :: verificationMetaTags}"></th:block>
 */
@ControllerAdvice
@RequiredArgsConstructor
public class VerificationMetaAdvice {

    private final SiteVerificationService verificationService;

    @ModelAttribute("yandexMeta")
    public SiteVerification yandexMeta() {
        return verificationService.getActiveMetaTag(SiteVerification.SearchEngine.YANDEX).orElse(null);
    }

    @ModelAttribute("googleMeta")
    public SiteVerification googleMeta() {
        return verificationService.getActiveMetaTag(SiteVerification.SearchEngine.GOOGLE).orElse(null);
    }
}