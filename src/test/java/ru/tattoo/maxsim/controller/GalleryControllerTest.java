package ru.tattoo.maxsim.controller;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GalleryControllerTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setup() {
        WebDriverManager.firefoxdriver().setup();

        // Создайте экземпляр WebDriver для Chrome
        driver = new FirefoxDriver();

        // Настройте WebDriverWait для ожидания загрузки элементов
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void teardown() {
        // Закройте браузер после завершения теста
        driver.quit();
    }

    @Test
    public void testGalleryPageLoad() {
        // Откройте главную страницу галереи
        driver.get("http://localhost:8080/gallery");

        // Подождите, пока страница загрузится
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        // Проверьте, что страница содержит слово "Галерея"
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Галерея"));
    }

    @Test
    public void testGallerySearch() {
        // Откройте страницу галереи
        driver.get("http://localhost:8080/gallery");

        // Введите стиль в поле поиска
        driver.findElement(By.id("search-style-input")).sendKeys("Портрет");

        // Нажмите кнопку поиска
        driver.findElement(By.id("search-button")).click();

        // Подождите, пока результаты поиска появятся
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-results")));

        // Проверьте, что результаты поиска содержат слово "Портрет"
        String searchResults = driver.findElement(By.className("search-results")).getText();
        assertTrue(searchResults.contains("Портрет"));
    }

    @Test
    public void testReviewsModal() {
        // Откройте страницу галереи
        driver.get("http://localhost:8080/gallery");

        // Нажмите кнопку открытия модального окна с отзывами
        driver.findElement(By.id("open-reviews-modal")).click();

        // Подождите, пока модальное окно откроется
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reviews-modal")));

        // Проверьте, что модальное окно содержит слово "Отзывы"
        String modalText = driver.findElement(By.id("reviews-modal")).getText();
        assertTrue(modalText.contains("Отзывы"));
    }
}