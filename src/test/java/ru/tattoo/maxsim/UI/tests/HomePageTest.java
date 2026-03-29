package ru.tattoo.maxsim.UI.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest;
import ru.tattoo.maxsim.UI.baseActions.TestListener;
import ru.tattoo.maxsim.UI.page.GalleryPage;
import ru.tattoo.maxsim.UI.page.HomePage;

import static org.junit.jupiter.api.Assertions.*;

@Epic("UI Тесты")
@Feature("Главная страница")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestListener.class)
@DisplayName("Тесты главной страницы")
public class HomePageTest extends BaseSeleniumTest {

    private HomePage homePage;

    @BeforeEach
    @Step("Инициализация главной страницы")
    public void setup() {
        homePage = new HomePage(driver);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.1.1 - Загрузка главной страницы")
    @Description("Проверяет, что главная страница успешно загружается")
    public void testHomePageLoad() {
        // Act
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Главная страница должна загрузиться");
        assertTrue(homePage.isTextPresent("Тату-студия Maxsim"),
                "Страница должна содержать название студии");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.1.2 - Отображение карусели")
    @Description("Проверяет, что карусель с изображениями отображается")
    public void testCarouselDisplay() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        int slidesCount = homePage.getSlidesCount();

        // Assert
        assertTrue(slidesCount > 0, "Карусель должна содержать хотя бы один слайд");
        Allure.addAttachment("Количество слайдов", String.valueOf(slidesCount));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.3 - Переход на страницу галереи")
    @Description("Проверяет переход на страницу галереи из главного меню")
    public void testNavigateToGallery() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        GalleryPage galleryPage = homePage.clickGalleryLink();
        galleryPage.waitForPreloaderDisappear();

        // Assert
        assertTrue(galleryPage.isTextPresent("Галерея"),
                "Должна открыться страница галереи");

    }
}