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
import java.util.List;

import static ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest.log;

@Getter
public class RegistrationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(name = "login")
    private WebElement usernameInput;

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(name = "confirmPassword")
    private WebElement confirmPasswordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    @FindBy(css = ".alert-danger, .error-message, .text-danger")
    private WebElement errorMessage;

    @FindBy(css = ".validation-error, .field-error")
    private List<WebElement> validationErrors;

    @FindBy(css = "a[href='/login']")
    private WebElement loginLink;

    @FindBy(css = ".registration-form")
    private WebElement registrationForm;

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    @Step("Ожидание загрузки страницы регистрации")
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(registrationForm));
        log.info("✅ Страница регистрации загружена");
    }

    @Step("Проверка загрузки страницы регистрации")
    public boolean isRegistrationPageLoaded() {
        try {
            return registrationForm.isDisplayed() || usernameInput.isDisplayed();
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

    @Step("Ввод email: {email}")
    public void enterEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    @Step("Ввод пароля")
    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    @Step("Подтверждение пароля")
    public void enterConfirmPassword(String password) {
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(password);
    }

    @Step("Отправка формы регистрации")
    public void submitRegistration() {
        submitButton.click();
        log.info("✅ Форма регистрации отправлена");
    }

    @Step("Проверка наличия ошибки несовпадения паролей")
    public boolean hasPasswordMismatchError() {
        try {
            return driver.getPageSource().contains("пароли не совпадают") ||
                    driver.getPageSource().contains("passwords do not match");
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка наличия ошибки валидации")
    public boolean hasValidationError() {
        try {
            return !validationErrors.isEmpty() ||
                    (errorMessage != null && errorMessage.isDisplayed());
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение текста ошибки")
    public String getErrorMessage() {
        try {
            return errorMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }

    @Step("Переход на страницу логина")
    public void clickLoginLink() {
        wait.until(ExpectedConditions.elementToBeClickable(loginLink)).click();
        log.info("✅ Переход на страницу логина");
    }
}