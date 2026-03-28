package ru.tattoo.maxsim.security;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Epic("Безопасность")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Тесты безопасности")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("3.1 Доступ к публичным страницам")
    @Story("Публичный доступ")
    class PublicAccessTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Главная страница должна быть доступна без авторизации")
        @Description("Проверяет, что любой пользователь может видеть главную страницу")
        void homePageShouldBePublic() throws Exception {
            Allure.step("Запрос GET / без авторизации");

            mockMvc.perform(get("/"))
                    .andExpect(status().isOk());

            Allure.addAttachment("Результат", "✅ Главная страница доступна");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Страница галереи должна быть доступна без авторизации")
        void galleryPageShouldBePublic() throws Exception {
            Allure.step("Запрос GET /gallery без авторизации");

            mockMvc.perform(get("/gallery"))
                    .andExpect(status().isOk());

            Allure.addAttachment("Результат", "✅ Страница галереи доступна");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Страница блога должна быть доступна без авторизации")
        void blogPageShouldBePublic() throws Exception {
            Allure.step("Запрос GET /blog без авторизации");

            mockMvc.perform(get("/blog"))
                    .andExpect(status().isOk());

            Allure.addAttachment("Результат", "✅ Страница блога доступна");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Страница контактов должна быть доступна без авторизации")
        void contactsPageShouldBePublic() throws Exception {
            Allure.step("Запрос GET /contacts без авторизации");

            mockMvc.perform(get("/contacts"))
                    .andExpect(status().isOk());

            Allure.addAttachment("Результат", "✅ Страница контактов доступна");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Страница мастера должна быть доступна без авторизации")
        void masterPageShouldBePublic() throws Exception {
            Allure.step("Запрос GET /master без авторизации");

            mockMvc.perform(get("/master"))
                    .andExpect(status().isOk());

            Allure.addAttachment("Результат", "✅ Страница мастера доступна");
        }
    }

    @Nested
    @DisplayName("3.2 Доступ к админ-панели")
    @Story("Авторизованный доступ")
    class AdminAccessTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Админ-панель должна быть недоступна без авторизации")
        @Description("Проверяет, что неавторизованный пользователь не может зайти в админку")
        void adminPanelShouldNotBeAccessibleWithoutAuth() throws Exception {
            Allure.step("Запрос GET /admin без авторизации");

            mockMvc.perform(get("/admin"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));

            Allure.addAttachment("Результат", "✅ Перенаправление на страницу логина");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Админ-панель должна быть доступна для пользователя с ролью ADMIN")
        @WithMockUser(roles = "ADMIN")
        void adminPanelShouldBeAccessibleForAdmin() throws Exception {
            Allure.step("Запрос GET /admin с ролью ADMIN");

            mockMvc.perform(get("/admin"))
                    .andExpect(status().isOk());

            Allure.addAttachment("Результат", "✅ Админ-панель доступна");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Админ-панель должна быть недоступна для пользователя с ролью USER")
        @WithMockUser(roles = "USER")
        void adminPanelShouldNotBeAccessibleForUser() throws Exception {
            Allure.step("Запрос GET /admin с ролью USER");

            mockMvc.perform(get("/admin"))
                    .andExpect(status().isForbidden());

            Allure.addAttachment("Результат", "✅ Доступ запрещен (403)");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Загрузка изображения должна требовать авторизацию")
        void imageUploadShouldRequireAuth() throws Exception {
            Allure.step("POST /hero/image-import без авторизации");

            mockMvc.perform(multipart("/hero/image-import"))
                    .andExpect(status().is3xxRedirection());

            Allure.addAttachment("Результат", "✅ Перенаправление на страницу логина");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("Удаление слайда должно требовать авторизацию")
        void deleteSlideShouldRequireAuth() throws Exception {
            Allure.step("GET /hero/delete-section/1 без авторизации");

            mockMvc.perform(get("/hero/delete-section/1"))
                    .andExpect(status().is3xxRedirection());

            Allure.addAttachment("Результат", "✅ Перенаправление на страницу логина");
        }
    }
}