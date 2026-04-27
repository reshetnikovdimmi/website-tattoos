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
public class HomePage {

    private static final Logger log = LoggerFactory.getLogger(HomePage.class);
    public static final String URL = "http://localhost:8080/";

    @FindBy(id = "preloader")
    private WebElement preloader;

    @FindBy(css = ".hero-section .carousel-item")
    private List<WebElement> carouselSlides;

    @FindBy(css = ".carousel-item.active")
    private WebElement activeSlide;

    @FindBy(css = ".carousel-control-prev")
    private WebElement carouselPrevButton;

    @FindBy(css = ".carousel-control-next")
    private WebElement carouselNextButton;

    @FindBy(css = ".carousel")
    private WebElement carousel;

    @FindBy(css = ".about-section")
    private WebElement aboutSection;

    @FindBy(css = ".services-section .service-item")
    private List<WebElement> serviceItems;

    @FindBy(css = ".reviews-section .review-card")
    private List<WebElement> reviewCards;

    @FindBy(css = ".advantages-section .advantage-item")
    private List<WebElement> advantageItems;

    @FindBy(css = ".footer .social-links a")
    private List<WebElement> socialLinks;

    @FindBy(css = ".footer .phone")
    private WebElement phoneElement;

    @FindBy(css = ".footer .email")
    private WebElement emailElement;

    @FindBy(css = ".footer .address")
    private WebElement addressElement;

    @FindBy(css = ".navbar-nav a[href='/gallery']")
    private WebElement galleryLink;

    @FindBy(css = ".navbar-nav a[href='/sketches']")
    private WebElement sketchesLink;

    @FindBy(css = ".navbar-nav a[href='/contact']")
    private WebElement contactLink;

    @FindBy(css = ".slicknav_menu")
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
            log.info("✅ Прелоадер исчез");
        } catch (Exception e) {
            log.warn("Прелоадер не найден или уже исчез: {}", e.getMessage());
        }
    }

    @Step("Проверка загрузки главной страницы")
    public boolean isHomePageLoaded() {
        try {
            return carousel.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка наличия текста на странице")
    public boolean isTextPresent(String text) {
        String pageSource = driver.getPageSource();
        boolean present = pageSource.contains(text);
        log.info("Текст '{}' {} на странице", text, present ? "присутствует" : "отсутствует");
        return present;
    }

    @Step("Получение количества слайдов")
    public int getSlidesCount() {
        return carouselSlides.size();
    }

    @Step("Проверка видимости карусели")
    public boolean isCarouselVisible() {
        try {
            return carousel.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение текста активного слайда")
    public String getActiveSlideText() {
        return activeSlide.getText();
    }

    @Step("Нажатие кнопки следующего слайда")
    public void clickCarouselNext() {
        wait.until(ExpectedConditions.elementToBeClickable(carouselNextButton)).click();
        log.info("➡️ Нажата кнопка следующего слайда");
        wait.until(ExpectedConditions.attributeToBe(carouselNextButton, "aria-disabled", "false"));
    }

    @Step("Нажатие кнопки предыдущего слайда")
    public void clickCarouselPrev() {
        wait.until(ExpectedConditions.elementToBeClickable(carouselPrevButton)).click();
        log.info("⬅️ Нажата кнопка предыдущего слайда");
        wait.until(ExpectedConditions.attributeToBe(carouselPrevButton, "aria-disabled", "false"));
    }

    @Step("Проверка видимости блока 'О нас'")
    public boolean isAboutSectionVisible() {
        try {
            return aboutSection.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение текста блока 'О нас'")
    public String getAboutSectionText() {
        return aboutSection.getText();
    }

    @Step("Проверка видимости блока услуг")
    public boolean isServicesSectionVisible() {
        try {
            return !serviceItems.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение количества услуг")
    public int getServicesCount() {
        return serviceItems.size();
    }

    @Step("Проверка видимости блока отзывов")
    public boolean isReviewsSectionVisible() {
        try {
            return !reviewCards.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение количества отзывов")
    public int getReviewsCount() {
        return reviewCards.size();
    }

    @Step("Проверка видимости блока преимуществ")
    public boolean isAdvantagesSectionVisible() {
        try {
            return !advantageItems.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение количества преимуществ")
    public int getAdvantagesCount() {
        return advantageItems.size();
    }

    @Step("Проверка наличия социальных ссылок")
    public boolean hasSocialLinks() {
        return !socialLinks.isEmpty();
    }

    @Step("Проверка видимости телефона")
    public boolean isPhoneVisible() {
        try {
            return phoneElement.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка видимости email")
    public boolean isEmailVisible() {
        try {
            return emailElement.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка видимости адреса")
    public boolean isAddressVisible() {
        try {
            return addressElement.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Переход на страницу галереи")
    public GalleryPage clickGalleryLink() {
        wait.until(ExpectedConditions.elementToBeClickable(galleryLink)).click();
        log.info("🖼️ Переход на страницу галереи");
        return new GalleryPage(driver);
    }

    @Step("Переход на страницу эскизов")
    public SketchesPage clickSketchesLink() {
        wait.until(ExpectedConditions.elementToBeClickable(sketchesLink)).click();
        log.info("✏️ Переход на страницу эскизов");
        return new SketchesPage(driver);
    }

    @Step("Переход на страницу контактов")
    public ContactPage clickContactLink() {
        wait.until(ExpectedConditions.elementToBeClickable(contactLink)).click();
        log.info("📞 Переход на страницу контактов");
        return new ContactPage(driver);
    }

    @Step("Проверка видимости мобильного меню")
    public boolean isMobileMenuVisible() {
        try {
            return mobileMenu.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    @Step("Ожидание загрузки главной страницы")
    public void waitForPageLoad() {
        try {
            wait.until(ExpectedConditions.visibilityOf(carousel));
            log.info("✅ Главная страница загружена");
        } catch (Exception e) {
            log.warn("Ошибка загрузки главной страницы: {}", e.getMessage());
        }
    }

    @Step("Проверка видимости слайда по заголовку и подзаголовку")
    public boolean isCarouselSlideVisible(String title, String subtitle) {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(carouselSlides));
            for (WebElement slide : carouselSlides) {
                String slideText = slide.getText();
                if (slideText.contains(title) && slideText.contains(subtitle)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("Ошибка при проверке слайда: {}", e.getMessage());
            return false;
        }
    }

    @Step("Проверка наличия навигации карусели")
    public boolean hasCarouselNavigation() {
        try {
            return carouselPrevButton.isDisplayed() && carouselNextButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка видимости эскиза по описанию")
    public boolean isSketchVisible(String description) {
        try {
            List<WebElement> sketches = driver.findElements(By.cssSelector(".sketches-item, .sketch-item"));
            for (WebElement sketch : sketches) {
                if (sketch.getText().contains(description)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("Ошибка при проверке эскиза: {}", e.getMessage());
            return false;
        }
    }
    @Step("Проверка отображения имени пользователя: {username}")
    public boolean isUsernameDisplayed(String username) {
        try {
            WebElement userInfo = driver.findElement(By.cssSelector(".user-info, .user-name, .navbar-user"));
            return userInfo.getText().contains(username);
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка видимости отзыва: {comment}")
    public boolean isReviewVisible(String comment) {
        try {
            List<WebElement> reviews = driver.findElements(By.cssSelector(".reviews .card-body"));
            for (WebElement review : reviews) {
                if (review.getText().contains(comment)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}