package UI.baseActions;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.platform.commons.logging.LoggerFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.Method;
import java.util.logging.Logger;


public class CustomEventListener implements WebDriverListener {

  //  private static Logger logger = (Logger) LoggerFactory.getLogger(EventFiringDecorator.class);

    @Override
    @Step("Открыть страницу галереи")
    public void beforeClick (WebElement element) {
        System.out.println("B"+"--"+element.getTagName());
        Allure.step(element.getTagName());

    }
    @Override
    public void afterClick (WebElement element) {
        System.out.println("A"+"--"+element.getTagName());

        Allure.step(element.getTagName());

    }
    @Override
    public void beforeAnyWebDriverCall(WebDriver driver, Method method, Object[] args) {

        System.out.println("beforeAnyWebDriverCall");
    }
    @Override
    public void afterGet(WebDriver driver, String url) {
        Allure.step(url);
        System.out.println("after get"+"--"+url);
    }
    @Override
    public void afterSubmit(WebElement element) {
        Allure.step(element.getTagName());
        System.out.println("S"+"--"+element.getTagName());
    }
}
