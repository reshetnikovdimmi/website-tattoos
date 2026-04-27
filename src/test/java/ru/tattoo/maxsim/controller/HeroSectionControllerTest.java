package ru.tattoo.maxsim.controller;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.tattoo.maxsim.controller.admin.HeroSectionController;
import ru.tattoo.maxsim.service.interf.HeroSectionService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.storage.ImageStorage;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HeroSectionController.class)
@DisplayName("Тесты HeroSectionController")
public class HeroSectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private HeroSectionService heroSectionService;

    @Mock
    private HomeService homeService;

    @Mock
    private ImageStorage imageStorage;

    @Nested
    @DisplayName("2.2.1 Получение главной страницы")
    class GetHomePageTests {

        @Test
        @DisplayName("GET / - должен вернуть главную страницу")
        void getHomePage_ShouldReturnHomePage() throws Exception {
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
            Allure.addAttachment("Результат", "Главная страница загружена");
        }

    }

    @Nested
    @DisplayName("2.2.2 Админ-панель")
    class AdminPanelTests {

        @Test
        @DisplayName("GET /admin - без авторизации должен редиректить на логин")
        void getAdminPage_WithoutAuth_ShouldRedirectToLogin() throws Exception {
            mockMvc.perform(get("/admin"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
            Allure.addAttachment("Результат", "Пользователь перенаправлен на страницу логина");
        }
    }

    @Nested
    @DisplayName("2.2.3 Загрузка изображения")
    class ImageUploadTests {

        @Test
        @DisplayName("POST /hero/image-import - должен загрузить изображение")
        void uploadImage_ShouldSaveImage() throws Exception {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    "test content".getBytes()
            );

            doNothing().when(heroSectionService).saveImg(any(), any());

            // Act & Assert
            mockMvc.perform(multipart("/hero/image-import")
                            .file(file)
                            .param("section", "home"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin"));

            verify(heroSectionService, times(1)).saveImg(any(), any());
            Allure.addAttachment("Результат", "Изображение успешно загружено");
        }
    }

    @Nested
    @DisplayName("2.2.4 Удаление слайда")
    class DeleteSlideTests {

        @Test
        @DisplayName("GET /hero/delete-section/{id} - должен удалить слайд")
        void deleteSlide_ShouldDeleteSlide() throws Exception {
            // Arrange
            Long id = 1L;
            doNothing().when(heroSectionService).deleteById(id);

            // Act & Assert
            mockMvc.perform(get("/hero/delete-section/{id}", id))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin"));

            verify(heroSectionService, times(1)).deleteById(id);
            Allure.addAttachment("Результат", "Слайд успешно удален");
        }
    }
}
