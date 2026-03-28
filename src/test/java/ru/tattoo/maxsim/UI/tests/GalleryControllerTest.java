package ru.tattoo.maxsim.UI.tests;

import ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest;
import ru.tattoo.maxsim.UI.baseActions.TestListener;
import ru.tattoo.maxsim.UI.page.GalleryPage;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

@Slf4j
@Epic("UI Тесты")
@Feature("Страница галереи")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestListener.class)
@DisplayName("Тесты галереи")
public class GalleryControllerTest extends BaseSeleniumTest {

    private GalleryPage galleryPage;

    @BeforeEach
    @Step("Инициализация страницы галереи")
    public void setup() {
        galleryPage = new GalleryPage(driver);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.2.1 - Загрузка страницы галереи")
    @Description("Проверяет, что страница галереи успешно загружается и содержит слово 'Галерея'")
    @Owner("Maxsim Tattoo Team")
    @Link(name = "Страница галереи", url = "http://localhost:8080/gallery")
    public void testGalleryPageLoad() {
        // Act
        navigateTo(GalleryPage.URL);
        galleryPage.waitForPreloaderDisappear();
        galleryPage.waitForGalleryImagesVisibility();

        // Assert
        assertTrue(galleryPage.isTextPresent("Галерея"),
                "Страница должна содержать слово 'Галерея'");

        int imagesCount = galleryPage.countGalleryImages();
        assertTrue(imagesCount > 0, "Галерея должна содержать хотя бы одно изображение");

        Allure.addAttachment("Количество изображений", String.valueOf(imagesCount));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.2.2 - Нажатие правой кнопки навигации")
    @Description("Проверяет, что при нажатии правой кнопки изображения меняются")
    public void testImagesChangeWhenRightClicked() {
        // Arrange
        driver.get(GalleryPage.URL);
        galleryPage.waitForPreloaderDisappear();
        galleryPage.waitForGalleryImagesVisibility();

        // ШАГ 1: Фиксируем исходные изображения
        List<String> originalImageSources = galleryPage.getAllImageSrcs();

        // ШАГ 2: Кликаем правую кнопку
        galleryPage.clickRightButton();

        // ШАГ 3: Ждём, пока изображения сменятся
        galleryPage.waitForImagesToChange(originalImageSources);

        // ШАГ 4: Проверяем, что изображения действительно сменились
        List<String> updatedImageSources = galleryPage.getAllImageSrcs();
        assertNotEquals(originalImageSources, updatedImageSources,
                "Изображения должны смениться после нажатия правой кнопки");

        Allure.addAttachment("Новые изображения", updatedImageSources.toString());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.2.3 - Нажатие левой кнопки навигации")
    @Description("Проверяет, что при нажатии левой кнопки можно вернуться к предыдущим изображениям")
    public void testLeftButtonClickIfDisplayed() {
        // Arrange
        navigateTo(GalleryPage.URL);
        galleryPage.waitForPreloaderDisappear();
        galleryPage.waitForGalleryImagesVisibility();

        // Фиксируем исходные изображения
        List<String> originalImageSources = galleryPage.getAllImageSrcs();
        Allure.addAttachment("Исходные изображения", originalImageSources.toString());

        // Переходим вперед
        assertTrue(galleryPage.isRightButtonDisplayedAndEnabled(),
                "Правая кнопка должна быть доступна");
        galleryPage.clickRightButton();
        galleryPage.waitForImagesToChange(originalImageSources);

        // Проверяем, что левая кнопка стала доступна
        assertTrue(galleryPage.isLeftButtonDisplayedAndEnabled(),
                "После нажатия правой кнопки левая должна стать доступной");

        // Возвращаемся назад
        galleryPage.clickLeftButton();
        galleryPage.waitForImagesToChange(galleryPage.getAllImageSrcs());

        // Assert - проверяем, что вернулись к исходным изображениям
        List<String> returnedImageSources = galleryPage.getAllImageSrcs();
        assertEquals(originalImageSources, returnedImageSources,
                "После нажатия левой кнопки должны вернуться исходные изображения");

        Allure.addAttachment("Возвращенные изображения", returnedImageSources.toString());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.2.4 - Проверка количества изображений в галерее")
    @Description("Проверяет, что галерея отображает корректное количество изображений")
    public void testGalleryImagesCount() {
        // Arrange
        navigateTo(GalleryPage.URL);
        galleryPage.waitForPreloaderDisappear();
        galleryPage.waitForGalleryImagesVisibility();

        // Act
        int imagesCount = galleryPage.countGalleryImages();

        // Assert
        assertTrue(imagesCount >= 0, "Количество изображений не может быть отрицательным");
        assertTrue(imagesCount <= 50, "Слишком много изображений, возможна проблема с пагинацией");

        Allure.addAttachment("Всего изображений", String.valueOf(imagesCount));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.2.5 - Проверка уникальности изображений")
    @Description("Проверяет, что все изображения в галерее уникальны")
    public void testAllImagesAreUnique() {
        // Arrange
        navigateTo(GalleryPage.URL);
        galleryPage.waitForPreloaderDisappear();
        galleryPage.waitForGalleryImagesVisibility();

        // Act
        List<String> allImageSrcs = galleryPage.getAllImageSrcs();
        long uniqueCount = allImageSrcs.stream().distinct().count();

        // Assert
        assertEquals(allImageSrcs.size(), uniqueCount,
                "Все изображения в галерее должны быть уникальными");

        Allure.addAttachment("Всего изображений", String.valueOf(allImageSrcs.size()));
        Allure.addAttachment("Уникальных изображений", String.valueOf(uniqueCount));
    }
}