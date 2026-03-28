package ru.tattoo.maxsim.UI.baseActions;


import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.support.events.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.time.Duration;

abstract public class BaseSeleniumTest {

    protected static WebDriver driver;
    protected static WebDriverWait wait;

    public static final Logger log = LoggerFactory.getLogger(BaseSeleniumTest.class);

    @BeforeAll
    public static void init() {
        setUp();
    }

    public static void setUp() {
        log.info("🚀 Инициализация WebDriver для UI тестов");

        // Настройка GeckoDriver (для Chrome)
        WebDriverManager.chromedriver().setup();

        // Создание EventFiringDecorator и регистрация слушателей
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Безголовый режим для CI/CD
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");

        // Создание EventFiringDecorator и регистрация слушателей
        EventFiringDecorator eventFiringDecorator = new EventFiringDecorator(new CustomEventListener());

        // Создание экземпляра WebDriver
        driver = eventFiringDecorator.decorate(new ChromeDriver(options));

        // Настройка WebDriverWait
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        log.info("✅ WebDriver успешно инициализирован");

    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            takeScreenshot();
            driver.quit();
            log.info("✅ WebDriver закрыт");
        }
    }

    protected static void takeScreenshot() {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Скриншот", "image/png", new ByteArrayInputStream(screenshot), "png");
        } catch (Exception e) {
            log.warn("Не удалось сделать скриншот: {}", e.getMessage());
        }
    }

    protected void navigateTo(String url) {
        log.info("🌐 Переход на страницу: {}", url);
        driver.get(url);
        Allure.addAttachment("URL", url);
    }

}
