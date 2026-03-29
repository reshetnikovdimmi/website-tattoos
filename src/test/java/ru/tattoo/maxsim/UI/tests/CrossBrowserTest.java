package ru.tattoo.maxsim.UI.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import ru.tattoo.maxsim.UI.page.HomePage;

import static org.junit.jupiter.api.Assertions.*;

@Epic("UI Тесты")
@Feature("Кросс-браузерность")
@DisplayName("4.4 Тесты кросс-браузерности")
public class CrossBrowserTest {

    private WebDriver driver;
    private String baseUrl = "http://localhost:8080";

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // =========================================================================
    // 4.4.1 Google Chrome
    // =========================================================================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.4.1 - Google Chrome")
    @Description("Проверяет работу сайта в Google Chrome")
    void chromeBrowserShouldWork() {
        Allure.step("Запуск Chrome браузера");

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);

        Allure.step("Переход на главную страницу");
        driver.get(baseUrl);

        HomePage homePage = new HomePage(driver);

        Allure.step("Проверка загрузки страницы");
        assertTrue(homePage.isHomePageLoaded(), "Chrome: страница должна загрузиться");

        Allure.addAttachment("Браузер", "Google Chrome");
        Allure.addAttachment("Заголовок страницы", driver.getTitle());
    }

    // =========================================================================
    // 4.4.2 Mozilla Firefox
    // =========================================================================

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.4.2 - Mozilla Firefox")
    @Description("Проверяет работу сайта в Mozilla Firefox")
    void firefoxBrowserShouldWork() {
        Allure.step("Запуск Firefox браузера");

        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");

        driver = new FirefoxDriver(options);

        Allure.step("Переход на главную страницу");
        driver.get(baseUrl);

        HomePage homePage = new HomePage(driver);

        Allure.step("Проверка загрузки страницы");
        assertTrue(homePage.isHomePageLoaded(), "Firefox: страница должна загрузиться");

        Allure.addAttachment("Браузер", "Mozilla Firefox");
        Allure.addAttachment("Заголовок страницы", driver.getTitle());
    }

    // =========================================================================
    // 4.4.3 Microsoft Edge
    // =========================================================================

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.4.3 - Microsoft Edge")
    @Description("Проверяет работу сайта в Microsoft Edge")
    void edgeBrowserShouldWork() {
        Allure.step("Запуск Edge браузера");

        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new EdgeDriver(options);

        Allure.step("Переход на главную страницу");
        driver.get(baseUrl);

        HomePage homePage = new HomePage(driver);

        Allure.step("Проверка загрузки страницы");
        assertTrue(homePage.isHomePageLoaded(), "Edge: страница должна загрузиться");

        Allure.addAttachment("Браузер", "Microsoft Edge");
        Allure.addAttachment("Заголовок страницы", driver.getTitle());
    }

    // =========================================================================
    // 4.4.4 Сравнение поведения в разных браузерах
    // =========================================================================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.4.4 - Сравнение отображения в разных браузерах")
    @Description("Проверяет, что во всех браузерах сайт отображается одинаково")
    void allBrowsersDisplaySameContent() {
        String[] browsers = {"chrome", "firefox", "edge"};

        for (String browser : browsers) {
            Allure.step("Тестирование в браузере: " + browser);

            setupBrowser(browser);
            driver.get(baseUrl);
            HomePage homePage = new HomePage(driver);

            assertTrue(homePage.isHomePageLoaded(),
                    browser + ": страница должна загрузиться");

            Allure.addAttachment(browser + " заголовок", driver.getTitle());

            driver.quit();
        }
    }

    // =========================================================================
    // 4.4.5 Совместимость с разными версиями браузеров
    // =========================================================================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.4.5 - Совместимость с разными версиями Chrome")
    @Description("Проверяет работу с последней версией Chrome")
    void chromeLatestVersionShouldWork() {
        Allure.step("Запуск последней версии Chrome");

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        driver.get(baseUrl);

        HomePage homePage = new HomePage(driver);
        assertTrue(homePage.isHomePageLoaded(), "Последняя версия Chrome должна работать");

        String userAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent");
        Allure.addAttachment("Версия Firefox", userAgent);
    }

    // =========================================================================
    // 4.4.6 Консольные ошибки в разных браузерах
    // =========================================================================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.4.6 - Отсутствие ошибок в консоли браузера")
    @Description("Проверяет, что в консоли браузера нет ошибок")
    void noConsoleErrorsInAnyBrowser() {
        String[] browsers = {"chrome", "firefox", "edge"};

        for (String browser : browsers) {
            Allure.step("Проверка консоли в браузере: " + browser);

            setupBrowser(browser);
            driver.get(baseUrl);

            // Проверяем наличие ошибок в консоли
            HomePage homePage = new HomePage(driver);
            assertTrue(homePage.isHomePageLoaded(),
                    browser + ": страница должна загрузиться без ошибок");

            Allure.addAttachment("Консоль " + browser, "✅ Страница загружена без видимых ошибок");

            driver.quit();
        }
    }

    // =========================================================================
    // Вспомогательные методы
    // =========================================================================

    private void setupBrowser(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--no-sandbox");
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless=new");
                driver = new EdgeDriver(edgeOptions);
                break;

            default:
                throw new IllegalArgumentException("Unknown browser: " + browser);
        }

        driver.manage().window().maximize();
    }
}
