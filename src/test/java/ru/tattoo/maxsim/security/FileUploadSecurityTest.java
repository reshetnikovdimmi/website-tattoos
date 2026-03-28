package ru.tattoo.maxsim.security;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Epic("Безопасность")
@Feature("Проверка загружаемых файлов")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Тесты проверки загружаемых файлов")
class FileUploadSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Валидация файлов")
    @DisplayName("3.5.1 - Только изображения должны загружаться")
    @Description("Проверяет, что загружать можно только файлы изображений")
    void onlyImagesShouldBeUploadable() throws Exception {
        MockMultipartFile phpFile = new MockMultipartFile(
                "file",
                "malicious.php",
                "application/x-php",
                "<?php system($_GET['cmd']); ?>".getBytes()
        );

        Allure.step("Попытка загрузить PHP файл");
        Allure.addAttachment("Файл", "malicious.php (PHP скрипт)");

        mockMvc.perform(multipart("/hero/image-import")
                        .file(phpFile)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin?error=*"));

        Allure.step("✅ PHP файл отклонен");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Валидация файлов")
    @DisplayName("3.5.2 - Проверка расширения файла")
    @Description("Проверяет, что загружаемый файл имеет правильное расширение")
    void fileExtensionShouldBeValid() throws Exception {
        MockMultipartFile txtFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );

        Allure.step("Попытка загрузить текстовый файл");
        Allure.addAttachment("Файл", "test.txt (не изображение)");

        mockMvc.perform(multipart("/hero/image-import")
                        .file(txtFile)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin?error=*"));

        Allure.step("✅ Текстовый файл отклонен");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Валидация файлов")
    @DisplayName("3.5.3 - Проверка размера файла")
    @Description("Проверяет, что размер файла не превышает лимит")
    void fileSizeShouldNotExceedLimit() throws Exception {
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11 MB
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        Allure.step("Попытка загрузить файл размером 11 MB");
        Allure.addAttachment("Размер файла", "11 MB (превышает лимит 10 MB)");

        mockMvc.perform(multipart("/hero/image-import")
                        .file(largeFile)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin?error=*"));

        Allure.step("✅ Файл превышающий лимит отклонен");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Валидация файлов")
    @DisplayName("3.5.4 - Проверка MIME-типа файла")
    @Description("Проверяет, что MIME-тип файла соответствует изображению")
    void mimeTypeShouldBeImage() throws Exception {
        MockMultipartFile fakeImage = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "not actually an image".getBytes()
        );

        Allure.step("Попытка загрузить файл с неправильным содержимым");
        Allure.addAttachment("Файл", "test.jpg (содержимое не изображение)");

        mockMvc.perform(multipart("/hero/image-import")
                        .file(fakeImage)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin?error=*"));

        Allure.step("✅ Файл с неверным MIME-типом отклонен");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Валидация файлов")
    @DisplayName("3.5.5 - Имя файла должно быть безопасным")
    @Description("Проверяет, что имя файла не содержит опасных символов")
    void fileNameShouldBeSafe() throws Exception {
        MockMultipartFile maliciousFile = new MockMultipartFile(
                "file",
                "../../../etc/passwd.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        Allure.step("Попытка загрузить файл с path traversal в имени");
        Allure.addAttachment("Имя файла", "../../../etc/passwd.jpg");

        mockMvc.perform(multipart("/hero/image-import")
                        .file(maliciousFile)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin?error=*"));

        Allure.step("✅ Файл с опасным именем отклонен");
    }
}