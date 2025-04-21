package UI.baseActions;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.support.events.*;


import java.time.Duration;
import java.util.concurrent.TimeUnit;

abstract public class BaseSeleniumTest {
    protected static WebDriver driver;
    public static WebDriverWait wait;


    public static void setUp() {
        // Настройка GeckoDriver (для Firefox)
        WebDriverManager.firefoxdriver().setup();

        // Создание EventFiringDecorator и регистрация слушателей
        EventFiringDecorator eventFiringDecorator = new EventFiringDecorator(new CustomEventListener());

        // Создание экземпляра WebDriver
        driver = eventFiringDecorator.decorate(new FirefoxDriver(new FirefoxOptions()));

        // Настройка WebDriverWait
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    @BeforeAll
    public static void init() {
        setUp();
    }

    @AfterAll
    public static void tearDown() {
        driver.close();
    }

}
