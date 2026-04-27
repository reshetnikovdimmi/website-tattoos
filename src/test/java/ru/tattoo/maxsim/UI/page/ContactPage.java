package ru.tattoo.maxsim.UI.page;

import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@Getter
public class ContactPage {

    private static final Logger log = LoggerFactory.getLogger(ContactPage.class);
    public static final String URL = "http://localhost:8080/contact";

    @FindBy(id = "preloader")
    private WebElement preloader;

    @FindBy(css = ".contact-form")
    private WebElement contactForm;

    @FindBy(name = "name")
    private WebElement nameInput;

    @FindBy(name = "subject")
    private WebElement subjectInput;

    @FindBy(name = "msgBody")
    private WebElement messageInput;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    @FindBy(css = ".success-message")
    private WebElement successMessage;

    @FindBy(css = ".error-message")
    private WebElement errorMessage;

    @FindBy(css = ".contact-info")
    private WebElement contactInfo;

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ContactPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    @Step("Ожидание исчезновения прелоадера")
    public void waitForPreloaderDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOf(preloader));
            log.info("✅ Прелоадер исчез");
        } catch (Exception e) {
            log.warn("Прелоадер не найден или уже исчез: {}", e.getMessage());
        }
    }

    @Step("Проверка наличия текста на странице")
    public boolean isTextPresent(String text) {
        String pageSource = driver.getPageSource();
        boolean present = pageSource.contains(text);
        log.info("Текст '{}' {} на странице", text, present ? "присутствует" : "отсутствует");
        return present;
    }

    @Step("Заполнение формы контактов")
    public void fillContactForm(String name, String subject, String message) {
        wait.until(ExpectedConditions.visibilityOf(contactForm));

        nameInput.clear();
        nameInput.sendKeys(name);

        subjectInput.clear();
        subjectInput.sendKeys(subject);

        messageInput.clear();
        messageInput.sendKeys(message);

        log.info("📝 Форма заполнена: name={}, subject={}", name, subject);
    }

    @Step("Отправка формы")
    public void submitForm() {
        submitButton.click();
        log.info("📨 Форма отправлена");
    }

    @Step("Проверка успешной отправки")
    public boolean isSuccessMessageDisplayed() {
        try {
            return successMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение текста сообщения об успехе")
    public String getSuccessMessageText() {
        return successMessage.getText();
    }

    @Step("Проверка отображения ошибки")
    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка видимости контактной информации")
    public boolean isContactInfoVisible() {
        try {
            return contactInfo.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}