package UI.page;

import UI.baseActions.BaseSeleniumTest;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds((10)));
        PageFactory.initElements(driver, this);
    }

    public void waitForPreloaderDisappear() {
        wait.until(ExpectedConditions.invisibilityOf(preloader));
    }

    public void waitForGalleryImagesPresence() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".gallery-item img")));
    }

    public void waitForGalleryImagesVisibility() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".gallery-item img")));
    }

    public void clickRightButton() {
        rightButton.click();
    }

    public void clickLeftButton() {
        leftButton.click();
    }

    public boolean isLeftButtonDisplayedAndEnabled() {
        return leftButton.isDisplayed() && leftButton.isEnabled();
    }

    public int countGalleryImages() {
        System.out.println(galleryImages.size());
        return galleryImages.size();
    }

    public String getFirstImageSrc() {
        return galleryImages.get(0).getAttribute("src");
    }

    public void waitForNumberOfImagesIncreased(int expectedCount) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".gallery-item img"), expectedCount));
    }

    public void waitForNumberOfImagesIncreased() {
        waitForNumberOfImagesIncreased(countGalleryImages());
    }
    public void waitForImagesToChange(List<String> originalImageSources) {
        wait.until(d -> {
            List<String> currentImageSources = d.findElements(By.cssSelector(".gallery-item img"))
                    .stream()
                    .map(img -> img.getAttribute("src"))
                    .collect(Collectors.toList());
            return !currentImageSources.equals(originalImageSources);
        });
    }



    public List<String> getAllImageSrcs() {
        return galleryImages.stream()
                .map(img -> img.getAttribute("src"))
                .collect(Collectors.toList());
    }

}