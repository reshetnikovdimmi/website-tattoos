package ru.tattoo.maxsim.UI.page;

import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest.log;

@Getter
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(css = ".map-contact-form")
    private WebElement loginForm;

    @FindBy(css = ".alert-danger, .error-message, .text-danger")
    private WebElement errorMessage;

    @FindBy(css = "a[href='/registration']")
    private WebElement registrationLink;

    @FindBy(css = ".logout-btn, a[href='/logout']")
    private WebElement logoutButton;

    @FindBy(css = ".user-info, .user-name")
    private WebElement userInfo;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    @Step("Ожидание загрузки страницы логина")
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(loginForm));
        log.info("✅ Страница логина загружена");
    }

    @Step("Проверка загрузки страницы логина")
    public boolean isLoginPageLoaded() {
        try {
            return loginForm.isDisplayed() || usernameInput.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Ввод логина: {username}")
    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }

    @Step("Ввод пароля")
    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    @Step("Отправка формы входа")
    public void submitLogin() {
        loginButton.click();
        log.info("✅ Форма входа отправлена");
    }

    @Step("Выполнение входа")
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        submitLogin();
    }

    @Step("Проверка наличия сообщения об ошибке")
    public boolean hasErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение текста ошибки")
    public String getErrorMessage() {
        return errorMessage.getText();
    }

    @Step("Проверка авторизации пользователя")
    public boolean isLoggedIn() {
        try {
            return userInfo.isDisplayed() || !driver.getCurrentUrl().contains("/login");
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка выхода из системы")
    public boolean isLoggedOut() {
        try {
            return driver.getCurrentUrl().contains("/login") || loginForm.isDisplayed();
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Выход из системы")
    public void logout() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
            log.info("✅ Выполнен выход из системы");
        } catch (Exception e) {
            log.warn("Кнопка выхода не найдена: {}", e.getMessage());
        }
    }

    @Step("Переход на страницу регистрации")
    public void clickRegistrationLink() {
        wait.until(ExpectedConditions.elementToBeClickable(registrationLink)).click();
        log.info("✅ Переход на страницу регистрации");
    }
}