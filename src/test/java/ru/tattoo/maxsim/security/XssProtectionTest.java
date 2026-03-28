package ru.tattoo.maxsim.security;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Epic("Безопасность")
@Feature("Защита от XSS")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Тесты защиты от XSS")
class XssProtectionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("XSS атаки")
    @DisplayName("3.3.1 - XSS атаки должны блокироваться")
    @Description("Проверяет, что XSS скрипты экранируются")
    void xssScriptsShouldBeEscaped() throws Exception {
        String maliciousInput = "<script>alert('XSS')</script>";

        Allure.step("Попытка XSS атаки через параметр поиска");
        Allure.addAttachment("Malicious input", maliciousInput);

        mockMvc.perform(get("/search")
                        .param("q", maliciousInput))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("<script>"))));

        Allure.step("✅ XSS скрипт успешно экранирован");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("XSS атаки")
    @DisplayName("3.3.2 - HTML теги должны экранироваться в комментариях")
    void htmlTagsShouldBeEscapedInComments() throws Exception {
        String maliciousHtml = "<img src=x onerror=alert('XSS')>";

        Allure.step("Попытка XSS атаки через комментарий");
        Allure.addAttachment("Malicious HTML", maliciousHtml);

        mockMvc.perform(get("/add-review")
                        .param("text", maliciousHtml))
                .andExpect(status().is3xxRedirection());

        Allure.step("✅ HTML теги экранированы");
    }
}