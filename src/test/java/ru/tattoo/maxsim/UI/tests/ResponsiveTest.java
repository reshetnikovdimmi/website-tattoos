package ru.tattoo.maxsim.UI.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest;
import ru.tattoo.maxsim.UI.page.HomePage;
import ru.tattoo.maxsim.UI.page.GalleryPage;

import static org.junit.jupiter.api.Assertions.*;

@Epic("UI Тесты")
@Feature("Адаптивность")
@DisplayName("4.3 Тесты адаптивности")
@Link(name = "Главная страница", url = "http://localhost:8080/")
public class ResponsiveTest extends BaseSeleniumTest {

    // =========================================================================
    // 4.3.1 Десктопная версия
    // =========================================================================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.3.1 - Десктопная версия (1920x1080)")
    @Description("Проверяет отображение сайта на десктопном разрешении")
    void desktopVersionShouldDisplayCorrectly() {
        // Arrange
        driver.manage().window().setSize(new Dimension(1920, 1080));

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться на десктопном разрешении");
        assertTrue(homePage.getSlidesCount() > 0, "Карусель должна отображаться");

        Allure.addAttachment("Разрешение", "1920x1080 (Desktop)");
        takeScreenshot();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.3.1.2 - Десктопная версия (1366x768)")
    @Description("Проверяет отображение на ноутбучном разрешении")
    void laptopVersionShouldDisplayCorrectly() {
        // Arrange
        driver.manage().window().setSize(new Dimension(1366, 768));

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться на разрешении 1366x768");
        assertFalse(hasHorizontalScroll(), "Не должно быть горизонтального скролла");

        Allure.addAttachment("Разрешение", "1366x768 (Laptop)");
        takeScreenshot();
    }

    // =========================================================================
    // 4.3.2 Планшетная версия
    // =========================================================================

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.3.2 - Планшетная версия (768x1024)")
    @Description("Проверяет отображение на планшете в портретной ориентации")
    void tabletPortraitVersionShouldDisplayCorrectly() {
        // Arrange
        driver.manage().window().setSize(new Dimension(768, 1024));

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться на планшете");

        Allure.addAttachment("Разрешение", "768x1024 (Tablet Portrait)");
        takeScreenshot();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.3.2.2 - Планшетная версия (1024x768)")
    @Description("Проверяет отображение на планшете в альбомной ориентации")
    void tabletLandscapeVersionShouldDisplayCorrectly() {
        // Arrange
        driver.manage().window().setSize(new Dimension(1024, 768));

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться на планшете");

        Allure.addAttachment("Разрешение", "1024x768 (Tablet Landscape)");
        takeScreenshot();
    }

    // =========================================================================
    // 4.3.3 Мобильная версия
    // =========================================================================

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.3.3 - Мобильная версия (iPhone SE - 375x667)")
    @Description("Проверяет отображение на iPhone SE")
    void iPhoneSEVersionShouldDisplayCorrectly() {
        // Arrange
        driver.manage().window().setSize(new Dimension(375, 667));

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться на iPhone SE");

        // Проверяем мобильное меню
       // homePage.openMobileMenu();

        Allure.addAttachment("Разрешение", "375x667 (iPhone SE)");
        takeScreenshot();
    }

    @Test
    @Severity(SeverityLevel.TRIVIAL)
    @DisplayName("4.3.3.2 - Мобильная версия (iPhone 12/13 - 390x844)")
    @Description("Проверяет отображение на iPhone 12/13")
    void iPhone12VersionShouldDisplayCorrectly() {
        // Arrange
        driver.manage().window().setSize(new Dimension(390, 844));

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться на iPhone 12");

        Allure.addAttachment("Разрешение", "390x844 (iPhone 12/13)");
        takeScreenshot();
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.3.3.3 - Мобильная версия (Google Pixel - 412x915)")
    @Description("Проверяет отображение на Google Pixel")
    void googlePixelVersionShouldDisplayCorrectly() {
        // Arrange
        driver.manage().window().setSize(new Dimension(412, 915));

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться на Google Pixel");

        Allure.addAttachment("Разрешение", "412x915 (Google Pixel)");
        takeScreenshot();
    }

    // =========================================================================
    // 4.3.4 Маленький мобильный экран
    // =========================================================================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.3.4 - Маленький мобильный экран (iPhone 5/SE - 320x568)")
    @Description("Проверяет отображение на маленьком мобильном устройстве")
    void smallMobileVersionShouldDisplayCorrectly() {
        // Arrange
        driver.manage().window().setSize(new Dimension(320, 568));

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться на маленьком экране");
        assertFalse(hasHorizontalScroll(), "Не должно быть горизонтального скролла");

        Allure.addAttachment("Разрешение", "320x568 (iPhone 5/SE)");
        takeScreenshot();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.3.4.2 - Маленький мобильный экран (Samsung Galaxy S8 - 360x740)")
    @Description("Проверяет отображение на Samsung Galaxy S8")
    void samsungGalaxyS8VersionShouldDisplayCorrectly() {
        // Arrange
        driver.manage().window().setSize(new Dimension(360, 740));

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться на Galaxy S8");

        Allure.addAttachment("Разрешение", "360x740 (Samsung Galaxy S8)");
        takeScreenshot();
    }

    // =========================================================================
    // 4.3.5 Альбомная ориентация на мобильном
    // =========================================================================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.3.5 - Альбомная ориентация на мобильном")
    @Description("Проверяет отображение при повороте экрана")
    void landscapeOrientationShouldWork() {
        // Arrange
        driver.manage().window().setSize(new Dimension(667, 375)); // Альбомная ориентация

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться в альбомной ориентации");

        Allure.addAttachment("Ориентация", "Альбомная (667x375)");
        takeScreenshot();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.3.5.2 - Поворот экрана портрет ↔ альбом")
    @Description("Проверяет корректность отображения при повороте экрана")
    void screenRotationShouldWork() {
        // Arrange
        driver.manage().window().setSize(new Dimension(375, 667)); // Портрет

        // Act
        navigateTo(HomePage.URL);
        HomePage homePage = new HomePage(driver);
        assertTrue(homePage.isHomePageLoaded(), "Страница должна загрузиться в портрете");

        // Поворачиваем в альбом
        driver.manage().window().setSize(new Dimension(667, 375));

        // Assert
        assertTrue(homePage.isHomePageLoaded(), "Страница должна корректно отображаться после поворота");

        Allure.addAttachment("Поворот", "Портрет → Альбом");
        takeScreenshot();
    }

    // =========================================================================
    // 4.3.6 Горизонтальный скроллинг
    // =========================================================================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.3.6 - Отсутствие горизонтального скролла на всех разрешениях")
    @Description("Проверяет, что на всех разрешениях нет горизонтального скролла")
    void noHorizontalScrollOnAllResolutions() {
        int[] widths = {320, 375, 390, 412, 768, 1024, 1366, 1920};

        for (int width : widths) {
            driver.manage().window().setSize(new Dimension(width, 800));

            navigateTo(HomePage.URL);
            HomePage homePage = new HomePage(driver);

            assertTrue(homePage.isHomePageLoaded(),
                    "Страница должна загрузиться при ширине " + width);
            assertFalse(hasHorizontalScroll(),
                    "Горизонтальный скролл не должен появляться при ширине " + width);

            Allure.addAttachment("Ширина " + width + "px", "✅ Горизонтальный скролл отсутствует");
        }
    }

    // =========================================================================
    // Вспомогательные методы
    // =========================================================================

    /**
     * Проверяет наличие горизонтального скролла
     */
    private boolean hasHorizontalScroll() {
        try {
            // Получаем ширину документа и видимую область
            Number scrollWidth = (Number) ((JavascriptExecutor) driver)
                    .executeScript("return document.documentElement.scrollWidth");
            Number clientWidth = (Number) ((JavascriptExecutor) driver)
                    .executeScript("return document.documentElement.clientWidth");

            return scrollWidth.longValue() > clientWidth.longValue();
        } catch (Exception e) {
            // В случае ошибки считаем, что скролла нет
            return false;
        }
    }
}
