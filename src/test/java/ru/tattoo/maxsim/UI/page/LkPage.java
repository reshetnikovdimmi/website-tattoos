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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

@Getter
public class LkPage {

    private static final Logger log = LoggerFactory.getLogger(LkPage.class);
    public static final String URL = "http://localhost:8080/lk";

    @FindBy(id = "preloader")
    private WebElement preloader;

    @FindBy(css = ".user-info, .user-name, .profile-name")
    private WebElement usernameElement;

    @FindBy(css = ".user-email, .profile-email")
    private WebElement emailElement;

    @FindBy(css = ".user-avatar img")
    private WebElement avatarImage;

    @FindBy(css = ".profile-edit-btn")
    private WebElement editProfileButton;

    @FindBy(css = ".user-tattoos")
    private List<WebElement> userTattoos;

    @FindBy(css = ".add-tattoo-btn")
    private WebElement addTattooButton;

    @FindBy(css = ".logout-btn")
    private WebElement logoutButton;

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LkPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    @Step("Ожидание загрузки страницы личного кабинета")
    public void waitForPageLoad() {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameElement));
            log.info("✅ Страница личного кабинета загружена");
        } catch (Exception e) {
            log.warn("Ошибка загрузки личного кабинета: {}", e.getMessage());
        }
    }

    @Step("Проверка отображения имени пользователя: {username}")
    public boolean isUsernameDisplayed(String username) {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameElement));
            return usernameElement.getText().contains(username);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка отображения email: {email}")
    public boolean isEmailDisplayed(String email) {
        try {
            wait.until(ExpectedConditions.visibilityOf(emailElement));
            return emailElement.getText().contains(email);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка отображения аватара")
    public boolean isAvatarDisplayed() {
        try {
            return avatarImage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение количества татуировок пользователя")
    public int getUserTattoosCount() {
        return userTattoos.size();
    }

    @Step("Нажатие кнопки редактирования профиля")
    public void clickEditProfile() {
        wait.until(ExpectedConditions.elementToBeClickable(editProfileButton)).click();
        log.info("✅ Нажата кнопка редактирования профиля");
    }

    @Step("Нажатие кнопки добавления тату")
    public void clickAddTattoo() {
        wait.until(ExpectedConditions.elementToBeClickable(addTattooButton)).click();
        log.info("✅ Нажата кнопка добавления тату");
    }

    @Step("Выход из личного кабинета")
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
        log.info("✅ Выполнен выход из личного кабинета");
    }
}