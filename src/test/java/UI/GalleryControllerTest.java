package UI;

import UI.baseActions.BaseSeleniumTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GalleryControllerTest extends BaseSeleniumTest  {

    @Test
    public void testGalleryPageLoad() {
        // Откройте главную страницу галереи
        driver.get("http://localhost:8080/gallery");
        // Ожидание исчезновения прелоадера
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("preloder")));
        // Проверьте, что страница содержит слово "Галерея"
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Галерея"));
    }

    @Test
    public void testButtonRightClick(){
        // Открытие страницы галереи
        driver.get("http://localhost:8080/gallery");

        // Ожидание исчезновения прелоадера
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("preloder")));

        // Клик по кнопке "right"
        driver.findElement(By.id("right")).click();

        // Ожидание появления изменений на странице (новые изображения)
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".gallery-item img"), 1));

        // Получение атрибута src у первого найденного изображения
        String newImageSrc = driver.findElement(By.cssSelector(".gallery-item img")).getAttribute("src");

        // Проверка, что атрибут src не равен null
        assertNotNull(newImageSrc, "Атрибут src должен быть не равен null");
    }

    @Test
    public void testLeftButtonClickIfDisplayed() throws InterruptedException {
        // Открытие страницы галереи
        driver.get("http://localhost:8080/gallery");

        // Ожидание исчезновения прелоадера
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("preloader")));

        driver.findElement(By.id("right")).click();

        // Ожидание появления изменений на странице (новые изображения)
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".gallery-item img"), 1));

        // Ожидание появления кнопки с id="left"
        WebElement leftButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("left")));

        // Проверка, отображается ли кнопка и активна ли она
        if (leftButton.isDisplayed() && leftButton.isEnabled()) {
            // Ожидание кликабельности кнопки
            wait.until(ExpectedConditions.elementToBeClickable(leftButton));

            // Нажатие на кнопку
            leftButton.click();

            // Ожидание появления изменений на странице (новые изображения)
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".gallery-item img"), 1));

            // Проверка, что произошли ожидаемые изменения (например, появление новой картинки)
            String newImageSrc = driver.findElement(By.cssSelector(".gallery-item img")).getAttribute("src");
            assertNotNull(newImageSrc, "Атрибут src должен быть не равен null");
        } else {
            // Если кнопка не отображается или не активна, можно добавить логирование или оставить тест без действий
            System.out.println("Кнопка 'left' не отображается или не активна.");
        }
    }

  /*  @Test
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
    }*/
}