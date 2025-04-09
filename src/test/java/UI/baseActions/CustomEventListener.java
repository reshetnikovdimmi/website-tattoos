package UI.baseActions;

import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.Method;


public class CustomEventListener implements WebDriverListener {
    @Override
    public void beforeClick (WebElement element) {
        Allure.step(element.getText());
        System.out.println(element.getText());
    }

    @Override
    public void afterGet(WebDriver driver, String url) {
        System.out.println("after get");
    }
}
