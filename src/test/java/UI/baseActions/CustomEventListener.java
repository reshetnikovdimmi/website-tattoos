package UI.baseActions;

import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.Method;


public class CustomEventListener implements WebDriverListener {
    @Override
    public void beforeClick (WebElement element) {
        System.out.println("B"+"--"+element.getTagName());
        Allure.step(element.getText());

    }
    @Override
    public void afterClick (WebElement element) {
        System.out.println("A"+"--"+element.getTagName());
        Allure.step(element.getText());

    }
    @Override
    public void beforeAnyWebDriverCall(WebDriver driver, Method method, Object[] args) {
        System.out.println("beforeAnyWebDriverCall");
    }
    @Override
    public void afterGet(WebDriver driver, String url) {
        System.out.println("after get");
    }
    @Override
    public void afterSubmit(WebElement element) {
        System.out.println("A"+"--"+element.getTagName());
        Allure.step(element.getText());
    }
}
