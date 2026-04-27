package ru.tattoo.maxsim.UI.page;

import io.qameta.allure.Step;
import lombok.Getter;
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
public class ReviewsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "preloader")
    private WebElement preloader;

    @FindBy(css = ".reviews .card")
    private List<WebElement> reviewCards;

    @FindBy(css = ".reviews .card-body")
    private List<WebElement> reviewComments;

    @FindBy(css = ".reviews .card-header")
    private List<WebElement> reviewAuthors;

    @FindBy(css = "textarea[name='comment']")
    private WebElement commentInput;

    @FindBy(css = "input[type='file'][name='file']")
    private WebElement fileInput;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    @FindBy(css = ".review-form")
    private WebElement reviewForm;

    @FindBy(css = ".success-message")
    private WebElement successMessage;

    public ReviewsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    @Step("Ожидание загрузки страницы отзывов")
    public void waitForPageLoad() {
        try {
            wait.until(ExpectedConditions.visibilityOf(reviewForm));
            log.info("✅ Страница отзывов загружена");
        } catch (Exception e) {
            log.warn("Форма отзывов не найдена: {}", e.getMessage());
        }
    }

    @Step("Получение количества отзывов")
    public int getReviewsCount() {
        return reviewCards.size();
    }

    @Step("Добавление отзыва: {comment}")
    public void addReview(String comment) {
        if (commentInput != null && commentInput.isDisplayed()) {
            commentInput.clear();
            commentInput.sendKeys(comment);
            submitButton.click();
            log.info("✅ Отзыв добавлен: {}", comment);
        } else {
            log.warn("Форма добавления отзыва недоступна");
        }
    }

    @Step("Проверка видимости отзыва: {comment}")
    public boolean isReviewCommentVisible(String comment) {
        for (WebElement review : reviewComments) {
            if (review.getText().contains(comment)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверка автора отзыва: {author}")
    public boolean isReviewAuthorVisible(String author) {
        for (WebElement authorElement : reviewAuthors) {
            if (authorElement.getText().contains(author)) {
                return true;
            }
        }
        return false;
    }

    @Step("Проверка доступности формы отзыва")
    public boolean isReviewFormAvailable() {
        try {
            return reviewForm != null && reviewForm.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение текста первого отзыва")
    public String getFirstReviewText() {
        if (reviewComments.isEmpty()) {
            return "";
        }
        return reviewComments.get(0).getText();
    }

    @Step("Получение текста сообщения об успехе")
    public String getSuccessMessage() {
        try {
            return successMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }
}