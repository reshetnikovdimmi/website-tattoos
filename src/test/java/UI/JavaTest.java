package UI;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class JavaTest {
    @Test
    public void Test(){
        // Настройка GeckoDriver (для Firefox)
        WebDriverManager.firefoxdriver().setup();

        // Создание экземпляра WebDriver
        WebDriver driver = new FirefoxDriver();

        // Переход на сайт
        driver.get("https://example.com");

        // Закрытие браузера после завершения тестов
        driver.quit();
    }
}
