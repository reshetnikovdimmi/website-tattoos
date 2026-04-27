package ru.tattoo.maxsim.UI.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest;
import ru.tattoo.maxsim.UI.baseActions.TestListener;
import ru.tattoo.maxsim.UI.page.GalleryPage;
import ru.tattoo.maxsim.UI.page.HomePage;
import ru.tattoo.maxsim.UI.page.SketchesPage;
import ru.tattoo.maxsim.UI.page.ContactPage;

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

        Allure.addAttachment("URL страницы", driver.getCurrentUrl());
        Allure.addAttachment("Заголовок страницы", driver.getTitle());
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
        assertTrue(homePage.isCarouselVisible(), "Карусель должна быть видима");

        Allure.addAttachment("Количество слайдов", String.valueOf(slidesCount));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.3 - Навигация по слайдам карусели")
    @Description("Проверяет работу кнопок навигации карусели")
    public void testCarouselNavigation() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        int slidesCount = homePage.getSlidesCount();

        if (slidesCount > 1) {
            // Act - Получаем текущий активный слайд
            String initialActiveSlide = homePage.getActiveSlideText();

            // Переключаем на следующий слайд
            homePage.clickCarouselNext();

            // Assert
            String newActiveSlide = homePage.getActiveSlideText();
            assertNotEquals(initialActiveSlide, newActiveSlide,
                    "После нажатия 'далее' слайд должен измениться");

            // Возвращаемся на предыдущий слайд
            homePage.clickCarouselPrev();
            String returnedSlide = homePage.getActiveSlideText();
            assertEquals(initialActiveSlide, returnedSlide,
                    "После нажатия 'назад' должен вернуться исходный слайд");

            Allure.addAttachment("Навигация", "Кнопки карусели работают корректно");
        } else {
            Allure.addAttachment("Статус", "Пропущен - недостаточно слайдов для навигации");
        }
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.4 - Переход на страницу галереи")
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
        assertTrue(galleryPage.countGalleryImages() > 0,
                "Страница галереи должна содержать изображения");

        Allure.addAttachment("Переход", "Галерея → OK");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.5 - Переход на страницу эскизов")
    @Description("Проверяет переход на страницу эскизов из главного меню")
    public void testNavigateToSketches() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        SketchesPage sketchesPage = homePage.clickSketchesLink();

        // Assert
        assertTrue(sketchesPage.isTextPresent("Эскизы"),
                "Должна открыться страница эскизов");

        Allure.addAttachment("Переход", "Эскизы → OK");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.6 - Переход на страницу контактов")
    @Description("Проверяет переход на страницу контактов из главного меню")
    public void testNavigateToContact() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        ContactPage contactPage = homePage.clickContactLink();

        // Assert
        assertTrue(contactPage.isTextPresent("Контакты"),
                "Должна открыться страница контактов");

        Allure.addAttachment("Переход", "Контакты → OK");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.7 - Отображение блока 'О нас'")
    @Description("Проверяет, что блок 'О нас' отображается на главной странице")
    public void testAboutSectionDisplay() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        boolean isAboutSectionVisible = homePage.isAboutSectionVisible();

        // Assert
        assertTrue(isAboutSectionVisible, "Блок 'О нас' должен отображаться");

        String aboutText = homePage.getAboutSectionText();
        assertNotNull(aboutText, "Текст блока 'О нас' не должен быть пустым");

        Allure.addAttachment("Текст блока 'О нас'", aboutText.substring(0, Math.min(aboutText.length(), 100)));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.8 - Отображение блока 'Наши услуги'")
    @Description("Проверяет, что блок услуг отображается на главной странице")
    public void testServicesSectionDisplay() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        boolean isServicesSectionVisible = homePage.isServicesSectionVisible();
        int servicesCount = homePage.getServicesCount();

        // Assert
        assertTrue(isServicesSectionVisible, "Блок 'Наши услуги' должен отображаться");
        assertTrue(servicesCount > 0, "Блок услуг должен содержать хотя бы одну услугу");

        Allure.addAttachment("Количество услуг", String.valueOf(servicesCount));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.9 - Отображение последних отзывов")
    @Description("Проверяет, что блок с последними отзывами отображается на главной странице")
    public void testReviewsSectionDisplay() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        boolean isReviewsSectionVisible = homePage.isReviewsSectionVisible();
        int reviewsCount = homePage.getReviewsCount();

        // Assert
        assertTrue(isReviewsSectionVisible, "Блок 'Отзывы' должен отображаться");

        if (reviewsCount > 0) {
            Allure.addAttachment("Количество отзывов", String.valueOf(reviewsCount));
        } else {
            Allure.addAttachment("Статус", "Отзывы отсутствуют (может быть нормально)");
        }
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.10 - Отображение блока 'Преимущества'")
    @Description("Проверяет, что блок с преимуществами отображается на главной странице")
    public void testAdvantagesSectionDisplay() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        boolean isAdvantagesVisible = homePage.isAdvantagesSectionVisible();
        int advantagesCount = homePage.getAdvantagesCount();

        // Assert
        assertTrue(isAdvantagesVisible, "Блок 'Преимущества' должен отображаться");
        assertTrue(advantagesCount >= 3, "Блок преимуществ должен содержать минимум 3 преимущества");

        Allure.addAttachment("Количество преимуществ", String.valueOf(advantagesCount));
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.1.11 - Отображение социальных ссылок")
    @Description("Проверяет, что ссылки на социальные сети отображаются в футере")
    public void testSocialLinksDisplay() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        boolean hasSocialLinks = homePage.hasSocialLinks();

        // Assert
        assertTrue(hasSocialLinks, "Ссылки на социальные сети должны отображаться");

        Allure.addAttachment("Социальные сети", "Присутствуют");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.1.12 - Отображение контактной информации")
    @Description("Проверяет, что контактная информация отображается в футере")
    public void testContactInfoDisplay() {
        // Arrange
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        boolean hasPhone = homePage.isPhoneVisible();
        boolean hasEmail = homePage.isEmailVisible();
        boolean hasAddress = homePage.isAddressVisible();

        // Assert
        assertTrue(hasPhone || hasEmail, "Должна отображаться хотя бы одна контактная информация");

        Allure.addAttachment("Телефон", String.valueOf(hasPhone));
        Allure.addAttachment("Email", String.valueOf(hasEmail));
        Allure.addAttachment("Адрес", String.valueOf(hasAddress));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.1.13 - Адаптивность: мобильное меню")
    @Description("Проверяет, что при уменьшении ширины окна появляется мобильное меню")
    public void testMobileMenuDisplay() {
        // Arrange
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667));
        navigateTo(HomePage.URL);
        homePage.waitForPreloaderDisappear();

        // Act
        boolean hasMobileMenu = homePage.isMobileMenuVisible();

        // Assert
        assertTrue(hasMobileMenu, "При мобильном разрешении должно отображаться мобильное меню");

        // Возвращаем исходный размер окна
        driver.manage().window().maximize();

        Allure.addAttachment("Разрешение", "375x667 (мобильное)");
    }
}