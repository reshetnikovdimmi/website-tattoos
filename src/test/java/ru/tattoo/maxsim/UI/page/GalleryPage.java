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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest.log;

@Getter
public class GalleryPage {

    public static final String URL = "http://localhost:8080/gallery";

    @FindBy(id = "preloader")
    private WebElement preloader;

    @FindBy(id = "right")
    private WebElement rightButton;

    @FindBy(id = "left")
    private WebElement leftButton;

    @FindBy(css = ".gallery-item img")
    private List<WebElement> galleryImages;

    private final WebDriver driver;
    private final WebDriverWait wait;

    public GalleryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds((15)));
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

    @Step("Ожидание появления изображений галереи")
    public void waitForGalleryImagesPresence() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".gallery-item img")));
        log.info("✅ Изображения галереи загружены");
    }

    @Step("Ожидание видимости изображений галереи")
    public void waitForGalleryImagesVisibility() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".gallery-item img")));
        log.info("✅ Изображения галереи видны");
    }

    @Step("Нажатие правой кнопки")
    public void clickRightButton() {
        wait.until(ExpectedConditions.elementToBeClickable(rightButton));
        rightButton.click();
        log.info("➡️ Нажата правая кнопка");
    }

    @Step("Нажатие левой кнопки")
    public void clickLeftButton() {
        wait.until(ExpectedConditions.elementToBeClickable(leftButton));
        leftButton.click();
        log.info("⬅️ Нажата левая кнопка");
    }

    @Step("Проверка доступности левой кнопки")
    public boolean isLeftButtonDisplayedAndEnabled() {
        try {
            boolean displayed = leftButton.isDisplayed();
            boolean enabled = leftButton.isEnabled();
            log.info("Левая кнопка: displayed={}, enabled={}", displayed, enabled);
            return displayed && enabled;
        } catch (Exception e) {
            log.warn("Левая кнопка не найдена");
            return false;
        }
    }

    @Step("Проверка доступности правой кнопки")
    public boolean isRightButtonDisplayedAndEnabled() {
        try {
            return rightButton.isDisplayed() && rightButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Подсчет количества изображений в галерее")
    public int countGalleryImages() {
        int count = galleryImages.size();
        log.info("📸 Количество изображений: {}", count);
        return count;
    }

    @Step("Получение src первого изображения")
    public String getFirstImageSrc() {
        return galleryImages.get(0).getAttribute("src");
    }

    @Step("Ожидание увеличения количества изображений")
    public void waitForNumberOfImagesIncreased(int expectedCount) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".gallery-item img"), expectedCount));
        log.info("✅ Количество изображений увеличилось");
    }

    @Step("Ожидание изменения изображений")
    public void waitForImagesToChange(List<String> originalImageSources) {
        wait.until(d -> {
            List<String> currentImageSources = d.findElements(By.cssSelector(".gallery-item img"))
                    .stream()
                    .map(img -> img.getAttribute("src"))
                    .collect(Collectors.toList());
            return !currentImageSources.equals(originalImageSources);
        });
        log.info("✅ Изображения изменились");
    }



    @Step("Получение всех src изображений")
    public List<String> getAllImageSrcs() {
        List<String> srcs = galleryImages.stream()
                .map(img -> img.getAttribute("src"))
                .collect(Collectors.toList());
        log.info("📸 Получено {} src изображений", srcs.size());
        return srcs;
    }

    @Step("Проверка наличия текста на странице")
    public boolean isTextPresent(String text) {
        String pageSource = driver.getPageSource();
        boolean present = pageSource.contains(text);
        log.info("Текст '{}' {} на странице", text, present ? "присутствует" : "отсутствует");
        return present;
    }
    @Step("Проверка наличия изображения с описанием")
    public boolean isImageWithDescriptionVisible(String description) {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(galleryImages));
            List<WebElement> descriptions = driver.findElements(By.cssSelector(".gallery-item .description, .reviews-admin .carousel-caption h4"));
            for (WebElement desc : descriptions) {
                if (desc.getText().contains(description)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("Ошибка при проверке описания: {}", e.getMessage());
            return false;
        }
    }

}