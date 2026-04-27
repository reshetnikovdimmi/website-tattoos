package ru.tattoo.maxsim.UI.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest;
import ru.tattoo.maxsim.UI.page.HomePage;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Функциональное тестирование")
@Feature("Пользовательские функции")
@DisplayName("5.4-5.6 Тесты пользовательских функций")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserFlowTest extends BaseSeleniumTest {

    private static final String TEST_USER = "testuser_" + System.currentTimeMillis();
    private static final String TEST_EMAIL = TEST_USER + "@test.com";
    private static final String TEST_PASSWORD = "Test123!@#";



    // =========================================================================
    // 5.4 Регистрация пользователя
    // =========================================================================

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("5.4.1 - Регистрация нового пользователя")
    @Description("Проверяет регистрацию нового пользователя")
    void userRegistration() {
        Allure.step("Переход на страницу регистрации");
        navigateTo("/registration");

        Allure.step("Заполнение формы регистрации");
        driver.findElement(By.name("login")).sendKeys(TEST_USER);
        driver.findElement(By.name("email")).sendKeys(TEST_EMAIL);
        driver.findElement(By.name("password")).sendKeys(TEST_PASSWORD);

        Allure.step("Отправка формы");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Allure.step("Проверка успешной регистрации");
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"), "Должен быть переход на страницу логина");

        Allure.addAttachment("Логин", TEST_USER);
        Allure.addAttachment("Email", TEST_EMAIL);
        takeScreenshot();
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("5.4.2 - Регистрация с уже существующим логином")
    @Description("Проверяет, что нельзя зарегистрироваться с существующим логином")
    void registrationWithExistingLogin() {
        Allure.step("Переход на страницу регистрации");
        navigateTo("/registration");

        Allure.step("Попытка регистрации с существующим логином");
        driver.findElement(By.name("login")).sendKeys(TEST_USER);
        driver.findElement(By.name("email")).sendKeys("different@test.com");
        driver.findElement(By.name("password")).sendKeys(TEST_PASSWORD);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Allure.step("Проверка, что регистрация не удалась");
        // Проверяем наличие сообщения об ошибке
        boolean hasError = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".error, .alert-danger"))).isDisplayed();

        assertTrue(hasError, "Должно быть сообщение об ошибке");

        // Проверяем, что остались на странице регистрации
        assertTrue(driver.getCurrentUrl().contains("/registration"),
                "Должны остаться на странице регистрации");

        takeScreenshot();
    }

    // =========================================================================
    // 5.5 Вход в систему
    // =========================================================================

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("5.5.1 - Вход в систему с корректными данными")
    @Description("Проверяет вход в систему с правильным логином и паролем")
     void loginWithValidCredentials() {
        Allure.step("Переход на страницу логина");
        navigateTo("/login");

        Allure.step("Ввод логина и пароля");
        driver.findElement(By.name("login")).sendKeys(TEST_USER);
        driver.findElement(By.name("password")).sendKeys(TEST_PASSWORD);

        Allure.step("Отправка формы");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Allure.step("Проверка успешного входа");
        wait.until(ExpectedConditions.urlContains("/"));

        HomePage homePage = new HomePage(driver);
        assertTrue(homePage.isTextPresent(TEST_USER), "Имя пользователя должно отображаться");

        Allure.addAttachment("Пользователь", TEST_USER);
        takeScreenshot();
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("5.5.2 - Вход в систему с неверным паролем")
    @Description("Проверяет, что вход с неверным паролем невозможен")
    void loginWithInvalidPassword() {
        Allure.step("Переход на страницу логина");
        navigateTo("/login");

        Allure.step("Ввод логина и неверного пароля");
        driver.findElement(By.name("login")).sendKeys(TEST_USER);
        driver.findElement(By.name("password")).sendKeys("wrongpassword");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Allure.step("Проверка, что вход не удался");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".error, .alert-danger")));

        // Проверяем, что остались на странице логина
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Должны остаться на странице логина");

        takeScreenshot();
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("5.5.3 - Выход из системы")
    @Description("Проверяет выход из системы")
    void logout() {
        Allure.step("Вход в систему");
        loginWithValidCredentials();

        Allure.step("Поиск кнопки выхода");
        // Находим и нажимаем кнопку выхода
        driver.findElement(By.cssSelector(".logout-link, [href*='logout']")).click();

        Allure.step("Проверка, что выход выполнен");
        HomePage homePage = new HomePage(driver);
        assertFalse(homePage.isTextPresent(TEST_USER), "Имя пользователя не должно отображаться");

        takeScreenshot();
    }

    // =========================================================================
    // 5.6 Добавление отзыва
    // =========================================================================

    @Test
    @Order(6)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("5.6.1 - Добавление отзыва пользователем")
    @Description("Проверяет, что авторизованный пользователь может добавить отзыв")
    void addReview() {
        Allure.step("Вход в систему");
        loginWithValidCredentials();

        Allure.step("Переход на страницу отзывов");
        navigateTo("/reviews");

        Allure.step("Заполнение формы отзыва");
        String reviewText = "Отличная тату-студия! " + System.currentTimeMillis();
        driver.findElement(By.name("comment")).sendKeys(reviewText);

        Allure.step("Добавление изображения (опционально)");
        // Если есть поле для загрузки изображения
        try {
            String filePath = getClass().getClassLoader().getResource("test-review.jpg").getPath();
            driver.findElement(By.name("file")).sendKeys(filePath);
        } catch (Exception e) {
            Allure.addAttachment("Изображение", "Не загружено");
        }

        Allure.step("Отправка отзыва");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Allure.step("Проверка, что отзыв добавлен");
        // Ждем обновления страницы
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".review-card, .review-item")));

        boolean reviewFound = driver.getPageSource().contains(reviewText);
        assertTrue(reviewFound, "Добавленный отзыв должен отображаться");

        Allure.addAttachment("Текст отзыва", reviewText);
        takeScreenshot();
    }

    @Test
    @Order(7)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("5.6.2 - Попытка добавить отзыв без авторизации")
    @Description("Проверяет, что неавторизованный пользователь не может добавить отзыв")
    void addReviewWithoutAuth() {
        Allure.step("Выход из системы");
        navigateTo("/logout");

        Allure.step("Переход на страницу отзывов");
        navigateTo("/reviews");

        Allure.step("Проверка, что форма отзыва недоступна");
        // Форма отзыва должна отсутствовать или быть заблокирована
        boolean formExists = driver.findElements(By.cssSelector("form")).size() > 0;

        // Если форма есть, проверяем, что она не отправляется
        if (formExists) {
            try {
                driver.findElement(By.cssSelector("button[type='submit']")).click();
                // Проверяем, что нас перенаправили на логин
                assertTrue(driver.getCurrentUrl().contains("/login"),
                        "Должно быть перенаправление на страницу логина");
            } catch (Exception e) {
                // Кнопка может быть неактивна
                Allure.addAttachment("Результат", "Форма отзыва недоступна");
            }
        } else {
            Allure.addAttachment("Результат", "Форма отзыва отсутствует");
        }

        takeScreenshot();
    }

}