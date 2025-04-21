package UI.baseActions;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;


public class CustomEventListener implements WebDriverListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomEventListener.class);

    @Override
    @Step("Открыть страницу галереи")
    public void beforeClick (WebElement element) {
        logAction("Открыть страницу галереи", element.getText());
        Allure.step(element.getTagName());

    }
    @Override
    @Step("После клика на элемент {0}")
    public void afterClick (WebElement element) {
        logAction("После клика на элемент", element.getTagName());
        Allure.step(element.getTagName());

    }
    @Override
    @Step("Перед выполнением любой команды WebDriver")
    public void beforeAnyWebDriverCall(WebDriver driver, Method method, Object[] args) {
        logAction("beforeAnyWebDriverCall", Objects.requireNonNull(driver.getTitle()));
    }
    @Override
    @Step("Переход на страницу {0}")
    public void afterGet(WebDriver driver, String url) {
        Allure.step(url);
        logAction("Переход на страницу {0}", url);
    }
    @Override
    @Step("Отправка формы {0}")
    public void afterSubmit(WebElement element) {
        Allure.step(element.getTagName());
        logAction("Отправка формы {0}", element.getTagName());
    }
    private void logAction(String actionType, String details) {
        if (!details.isEmpty()) {
            LOGGER.info(actionType + ": {}", details);
        } else {
            LOGGER.info(actionType);
        }
    }
}
