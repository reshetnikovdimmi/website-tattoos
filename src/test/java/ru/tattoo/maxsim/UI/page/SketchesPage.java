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
public class SketchesPage {

    private static final Logger log = LoggerFactory.getLogger(SketchesPage.class);
    public static final String URL = "http://localhost:8080/sketches";

    @FindBy(id = "preloader")
    private WebElement preloader;

    @FindBy(css = ".sketches-item")
    private List<WebElement> sketchesItems;

    @FindBy(css = ".sketches-item img")
    private List<WebElement> sketchesImages;

    @FindBy(css = ".sketches-item .description")
    private List<WebElement> sketchesDescriptions;

    @FindBy(css = ".pagination")
    private WebElement pagination;

    @FindBy(css = ".pagination .next")
    private WebElement nextPageButton;

    @FindBy(css = ".pagination .prev")
    private WebElement prevPageButton;

    private final WebDriver driver;
    private final WebDriverWait wait;

    public SketchesPage(WebDriver driver) {
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

    @Step("Получение количества эскизов")
    public int getSketchesCount() {
        int count = sketchesItems.size();
        log.info("📸 Количество эскизов: {}", count);
        return count;
    }

    @Step("Проверка наличия описания эскиза")
    public boolean isDescriptionPresent(String description) {
        for (WebElement desc : sketchesDescriptions) {
            if (desc.getText().contains(description)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверка доступности пагинации")
    public boolean isPaginationVisible() {
        try {
            return pagination.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Переход на следующую страницу")
    public void clickNextPage() {
        wait.until(ExpectedConditions.elementToBeClickable(nextPageButton)).click();
        log.info("➡️ Переход на следующую страницу");
    }

    @Step("Переход на предыдущую страницу")
    public void clickPrevPage() {
        wait.until(ExpectedConditions.elementToBeClickable(prevPageButton)).click();
        log.info("⬅️ Переход на предыдущую страницу");
    }

    @Step("Получение всех описаний эскизов")
    public List<String> getAllDescriptions() {
        return sketchesDescriptions.stream()
                .map(WebElement::getText)
                .collect(java.util.stream.Collectors.toList());
    }

    @Step("Получение всех src изображений")
    public List<String> getAllImageSrcs() {
        return sketchesImages.stream()
                .map(img -> img.getAttribute("src"))
                .collect(java.util.stream.Collectors.toList());
    }
}
