package UI.baseActions;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestListener implements TestWatcher {
    private final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void testSuccessful(ExtensionContext context) {
        // Выполняется, когда тест прошел успешно
        log.info("Test successful: {}", context.getDisplayName());
        attachDataTXT("Test successful:");
        attachScreenshotPNG("Test successful:");

    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        // Выполняется, когда тест завершился неудачно (например, из-за ошибки или несоответствия утверждений)
        log.error("Test failed: {}, Cause: {}", context.getDisplayName(), cause.getMessage());
        attachDataTXT("Test failed:");
        attachScreenshotPNG("Test failed:");

    }


    public void attachDataTXT(String s) {
        // Логи браузера временно отключены, так как поддержка прекращена
        /*Allure.addAttachment("Логи после успешного прохождения теста: ", String.valueOf( BaseSeleniumTest.driver.manage().logs().get(LogType.BROWSER).getAll()));
        WebDriverManager.chromedriver().quit();
        BaseSeleniumTest.driver.quit();*/
        // Все остальные необходимые шаги остаются прежними
        log.info("Сбор логов браузера временно невозможен.");
    }

    public void attachScreenshotPNG(String s){
        Allure.getLifecycle().addAttachment(
                s, "image/png", "png",
                ((TakesScreenshot)BaseSeleniumTest.driver).getScreenshotAs(OutputType.BYTES)
        );
    }
}