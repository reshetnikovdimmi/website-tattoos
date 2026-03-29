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

@Getter
public class HomePage {

    public static final String URL = "http://localhost:8080/";

    @FindBy(id = "preloader")
    private WebElement preloader;

    @FindBy(css = ".hero-carousel .slide")
    private List<WebElement> carouselSlides;

    @FindBy(css = ".hero-carousel")
    private WebElement carousel;

    @FindBy(linkText = "Галерея")
    private WebElement galleryLink;

    @FindBy(css = ".gallery-item")
    private List<WebElement> galleryItems;

    @FindBy(css = ".navbar-toggler")
    private WebElement mobileMenuToggle;

    @FindBy(css = ".navbar-collapse")
    private WebElement mobileMenu;

    private final WebDriver driver;
    private final WebDriverWait wait;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    @Step("Ожидание исчезновения прелоадера")
    public void waitForPreloaderDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOf(preloader));
        } catch (Exception e) {
            // Прелоадер может отсутствовать
        }
    }

    @Step("Проверка загрузки главной страницы")
    public boolean isHomePageLoaded() {
        return wait.until(ExpectedConditions.visibilityOf(carousel)).isDisplayed();
    }

    @Step("Получение количества слайдов в карусели")
    public int getSlidesCount() {
        return carouselSlides.size();
    }

    @Step("Переход на страницу галереи")
    public GalleryPage clickGalleryLink() {
        wait.until(ExpectedConditions.elementToBeClickable(galleryLink));
        galleryLink.click();
        return new GalleryPage(driver);
    }

    @Step("Открытие мобильного меню")
    public void openMobileMenu() {
        if (mobileMenuToggle.isDisplayed()) {
            mobileMenuToggle.click();
            wait.until(ExpectedConditions.visibilityOf(mobileMenu));
        }
    }

    @Step("Проверка наличия текста на странице")
    public boolean isTextPresent(String text) {
        String pageSource = driver.getPageSource();
        return pageSource.contains(text);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}