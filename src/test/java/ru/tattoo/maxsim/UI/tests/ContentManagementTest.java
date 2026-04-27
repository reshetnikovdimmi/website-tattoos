package ru.tattoo.maxsim.UI.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest;
import ru.tattoo.maxsim.UI.page.AdminPage;
import ru.tattoo.maxsim.UI.page.HomePage;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Функциональное тестирование")
@Feature("Управление контентом")
@DisplayName("5.1-5.3 Тесты управления контентом")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContentManagementTest extends BaseSeleniumTest {

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "admin"; // Замените на реальный пароль



    // =========================================================================
    // 5.1 Добавление слайда в карусель
    // =========================================================================

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("5.1.1 - Вход в админ-панель")
    @Description("Проверяет вход в админ-панель")
    void adminLogin() {
        Allure.step("Переход на страницу логина");
        navigateTo("/login");

        Allure.step("Ввод логина и пароля");
        driver.findElement(By.name("login")).sendKeys(ADMIN_LOGIN);
        driver.findElement(By.name("password")).sendKeys(ADMIN_PASSWORD);

        Allure.step("Отправка формы");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Allure.step("Проверка успешного входа");
        wait.until(ExpectedConditions.urlContains("/admin"));
        assertTrue(driver.getCurrentUrl().contains("/admin"), "Должен быть переход в админ-панель");

        Allure.addAttachment("URL после входа", driver.getCurrentUrl());
        takeScreenshot();
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("5.1.2 - Добавление слайда в карусель")
    @Description("Проверяет добавление нового слайда в карусель")
    void addSlideToCarousel() {
        Allure.step("Переход в админ-панель");
        navigateTo("/admin");

        Allure.step("Выбор раздела карусели");
        // Ждем загрузки админ-панели
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".carousel-form, [data-section='carousel']")));

        // Запоминаем количество слайдов до добавления
        int slidesBefore = getSlidesCount();
        Allure.addAttachment("Слайдов до добавления", String.valueOf(slidesBefore));

        Allure.step("Заполнение формы нового слайда");
        // Находим поле для ввода заголовка
        driver.findElement(By.name("title")).sendKeys("Тестовый слайд");
        driver.findElement(By.name("subtitle")).sendKeys("Подзаголовок тестового слайда");

        Allure.step("Загрузка изображения");
        // Находим поле для загрузки файла и загружаем тестовое изображение
        String filePath = getClass().getClassLoader().getResource("test-image.jpg").getPath();
        driver.findElement(By.name("file")).sendKeys(filePath);

        Allure.step("Сохранение слайда");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Ждем обновления страницы
        wait.until(ExpectedConditions.urlContains("/admin"));

        Allure.step("Проверка, что слайд добавлен");
        // Переходим на главную страницу
        navigateTo("/");
        HomePage homePage = new HomePage(driver);
        int slidesAfter = homePage.getSlidesCount();

        Allure.addAttachment("Слайдов после добавления", String.valueOf(slidesAfter));

        assertTrue(slidesAfter > slidesBefore, "Количество слайдов должно увеличиться");
        takeScreenshot();
    }

    // =========================================================================
    // 5.2 Редактирование слайда
    // =========================================================================

    @Test
    @Order(3)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("5.2.1 - Редактирование слайда")
    @Description("Проверяет редактирование существующего слайда")
    void editSlide() {
        Allure.step("Переход в админ-панель");
        navigateTo("/admin");

        Allure.step("Поиск кнопки редактирования первого слайда");
        // Находим кнопку редактирования
        By editButton = By.cssSelector(".edit-slide, [data-action='edit']");
        wait.until(ExpectedConditions.elementToBeClickable(editButton));

        String newTitle = "Отредактированный слайд " + System.currentTimeMillis();

        Allure.step("Редактирование заголовка");
        driver.findElement(editButton).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("title")));
        driver.findElement(By.name("title")).clear();
        driver.findElement(By.name("title")).sendKeys(newTitle);

        Allure.step("Сохранение изменений");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Allure.step("Проверка, что изменения сохранились");
        navigateTo("/");
        HomePage homePage = new HomePage(driver);

        assertTrue(homePage.isTextPresent(newTitle), "Новый заголовок должен отображаться на главной");

        Allure.addAttachment("Новый заголовок", newTitle);
        takeScreenshot();
    }

    // =========================================================================
    // 5.3 Удаление слайда
    // =========================================================================

    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("5.3.1 - Удаление слайда")
    @Description("Проверяет удаление существующего слайда")
    void deleteSlide() {
        Allure.step("Переход в админ-панель");
        navigateTo("/admin");

        // Запоминаем количество слайдов до удаления
        navigateTo("/");
        HomePage homePage = new HomePage(driver);
        int slidesBefore = homePage.getSlidesCount();
        Allure.addAttachment("Слайдов до удаления", String.valueOf(slidesBefore));

        Allure.step("Возврат в админ-панель");
        navigateTo("/admin");

        Allure.step("Удаление последнего слайда");
        // Находим кнопку удаления
        By deleteButton = By.cssSelector(".delete-slide, [data-action='delete']");
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
        driver.findElement(deleteButton).click();

        // Подтверждение удаления
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        Allure.step("Проверка, что слайд удален");
        navigateTo("/");
        int slidesAfter = homePage.getSlidesCount();

        Allure.addAttachment("Слайдов после удаления", String.valueOf(slidesAfter));

        assertTrue(slidesAfter < slidesBefore, "Количество слайдов должно уменьшиться");
        takeScreenshot();
    }

    // =========================================================================
    // Вспомогательные методы
    // =========================================================================

    private int getSlidesCount() {
        navigateTo("/");
        HomePage homePage = new HomePage(driver);
        return homePage.getSlidesCount();
    }
}
