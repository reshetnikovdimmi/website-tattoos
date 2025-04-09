package UI.baseActions;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
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
        // Создание экземпляра WebDriver
        driver = new FirefoxDriver(new FirefoxOptions());

        // Создание EventFiringDecorator и регистрация слушателей
        EventFiringDecorator decorator = new EventFiringDecorator(new CustomEventListener());
        WebDriver decoratedDriver = decorator.decorate(driver);
        // Настройте WebDriverWait для ожидания загрузки элементов
        wait = new WebDriverWait(decoratedDriver, Duration.ofSeconds(10));

        decoratedDriver.manage().window().maximize();
        decoratedDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        decoratedDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
    }

    @BeforeAll
    public static void init() {
        setUp();
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }

}
