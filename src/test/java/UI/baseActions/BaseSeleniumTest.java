package UI.baseActions;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

abstract public class BaseSeleniumTest {
    protected static WebDriver driver;
    public static WebDriverWait wait;

    public static void setUp() {
        // Настройка GeckoDriver (для Firefox)
        WebDriverManager.firefoxdriver().setup();
        // Создание экземпляра WebDriver
        driver = new FirefoxDriver();
        // Настройте WebDriverWait для ожидания загрузки элементов
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
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
