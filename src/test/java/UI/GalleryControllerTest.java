package UI;

import UI.baseActions.BaseSeleniumTest;
import UI.baseActions.TestListener;
import UI.page.GalleryPage;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

// Аннотация @Slf4j добавляет удобный доступ к слогу (логгеру)
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestListener.class)
public class GalleryControllerTest extends BaseSeleniumTest {

    private GalleryPage galleryPage;

    @BeforeEach
    public void setup() {
        galleryPage = new GalleryPage(driver);
    }

    @Test
    @DisplayName("Проверка загрузки главной страницы галереи")
    @Description("Тестирует загрузку страницы галереи и проверку наличия ключевого текста \"Галерея\".")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("John Doe")
    @Link(name = "Сайт", url = "http://localhost:8080/gallery")
    @Issue("AUTH-123")
    @TmsLink("TMS-456")
    public void testGalleryPageLoad() {
        driver.get(GalleryPage.URL);
        galleryPage.waitForPreloaderDisappear();
        galleryPage.waitForGalleryImagesVisibility();
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("Галерея"), "Страница должна содержать слово 'Галерея'");
    }

    @Test
    @DisplayName("Нажатие правой кнопки навигации")
    @Description("Тест проверяет нажатие кнопки \"Правая\", ожидая смену изображений.")
    @Severity(SeverityLevel.NORMAL)
    public void testImagesChangeWhenRightClicked() {
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
        assertFalse(updatedImageSources.equals(originalImageSources), "Изображения не сменились!");
    }

    @Test
    @DisplayName("Проверка левого перехода")
    @Description("Тестируется возможность возврата назад с использованием левой кнопки.")
    @Severity(SeverityLevel.NORMAL)
    public void testLeftButtonClickIfDisplayed() {
        // Заходим на сайт
        driver.get(GalleryPage.URL);
        galleryPage.waitForPreloaderDisappear();
        galleryPage.waitForGalleryImagesVisibility();

        // ШАГ 1: Фиксируем исходные изображения
        List<String> originalImageSources = galleryPage.getAllImageSrcs();

        // ШАГ 2: Переходим вперед (используем правую кнопку)
        galleryPage.clickRightButton();
        galleryPage.waitForImagesToChange(originalImageSources); // Ждём, пока изображения сменятся

        // ШАГ 3: Убеждаемся, что левая кнопка стала доступной
        if (galleryPage.isLeftButtonDisplayedAndEnabled()) {
            // ШАГ 4: Возврат назад с помощью левой кнопки
            galleryPage.clickLeftButton();
            galleryPage.waitForImagesToChange(galleryPage.getAllImageSrcs()); // Ждём ещё раз, чтобы убедиться, что вернулись к исходникам

            // ШАГ 5: Проверяем, что мы вернулись к исходным изображениям
            List<String> returnedImageSources = galleryPage.getAllImageSrcs();
            assertEquals(originalImageSources, returnedImageSources, "Возвращение назад привело к неправильным изображениям");
        } else {
            log.warn("Кнопка 'левый переход' не доступна или неактивна.");
        }
    }
}