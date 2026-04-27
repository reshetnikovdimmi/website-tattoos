package ru.tattoo.maxsim.UI.e2e;

import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest;
import ru.tattoo.maxsim.UI.baseActions.TestListener;
import ru.tattoo.maxsim.UI.page.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Epic("E2E Тестирование")
@Feature("Сценарий 6.2: Полный цикл пользователя - регистрация → вход → отзыв")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestListener.class)
@DisplayName("E2E: Полный цикл пользователя")
public class UserFlowE2ETest extends BaseSeleniumTest {

    private HomePage homePage;
    private LoginPage loginPage;
    private RegistrationPage registrationPage;
    private ReviewsPage reviewsPage;
    private LkPage lkPage;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String REGISTRATION_URL = BASE_URL + "/registration";
    private static final String REVIEWS_URL = BASE_URL + "/reviews";
    private static final String LK_URL = BASE_URL + "/lk";

    private static final String TEST_EMAIL_DOMAIN = "@e2etest.com";
    private static final String TEST_PASSWORD = "TestPassword123!";
    private static final String TEST_PASSWORD_SHORT = "123";
    private static final String TEST_EMAIL_INVALID = "invalid-email";
    private static final String TEST_LOGIN_SHORT = "ab";

    private String testUsername;
    private String testEmail;

    @BeforeEach
    @Step("Подготовка: инициализация страниц и генерация уникальных данных")
    public void setup() {
        // Генерация уникальных данных для теста
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        testUsername = "e2e_user_" + uniqueId;
        testEmail = testUsername + TEST_EMAIL_DOMAIN;

        homePage = new HomePage(driver);
        loginPage = new LoginPage(driver);
        registrationPage = new RegistrationPage(driver);
        reviewsPage = new ReviewsPage(driver);
        lkPage = new LkPage(driver);

        log.info("✅ Сгенерированы тестовые данные: username={}, email={}", testUsername, testEmail);
        Allure.addAttachment("Тестовый пользователь",
                String.format("Логин: %s, Email: %s, Пароль: %s", testUsername, testEmail, TEST_PASSWORD));
    }

    @AfterEach
    @Step("Очистка после теста")
    public void cleanup() {
        log.info("🧹 Очистка после теста");
        try {
            // Выход из системы после теста
            if (loginPage.isLoggedIn()) {
                loginPage.logout();
            }
        } catch (Exception e) {
            log.warn("Ошибка при выходе: {}", e.getMessage());
        }
    }

    // ==================== ТЕСТЫ РЕГИСТРАЦИИ ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("6.2.1 - Полный цикл: регистрация нового пользователя")
    @Description("Проверяет полный цикл регистрации нового пользователя с валидными данными")
    public void testUserRegistrationFullCycle() throws InterruptedException {
        // ========== ШАГ 1: Переход на страницу регистрации ==========
        Allure.step("1. Переход на страницу регистрации");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        assertTrue(registrationPage.isRegistrationPageLoaded(),
                "Страница регистрации должна загрузиться");

        // ========== ШАГ 2: Заполнение формы регистрации ==========
        Allure.step("2. Заполнение формы регистрации валидными данными");
        registrationPage.enterUsername(testUsername);
        registrationPage.enterEmail(testEmail);
        registrationPage.enterPassword(TEST_PASSWORD);
        registrationPage.enterConfirmPassword(TEST_PASSWORD);

        // ========== ШАГ 3: Отправка формы ==========
        Allure.step("3. Отправка формы регистрации");
        registrationPage.submitRegistration();
        Thread.sleep(1500);

        // ========== ШАГ 4: Проверка перенаправления на страницу логина ==========
        Allure.step("4. Проверка успешной регистрации");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "После успешной регистрации должен быть переход на страницу логина. Текущий URL: " + currentUrl);

        // ========== ШАГ 5: Проверка возможности входа с новыми данными ==========
        Allure.step("5. Проверка входа с зарегистрированными данными");
        loginPage.enterUsername(testUsername);
        loginPage.enterPassword(TEST_PASSWORD);
        loginPage.submitLogin();
        Thread.sleep(1500);

        boolean isLoggedIn = loginPage.isLoggedIn();
        assertTrue(isLoggedIn, "Должен быть успешный вход с зарегистрированными данными");

        Allure.addAttachment("Статус", "✅ Регистрация пользователя выполнена успешно");
        log.info("✅ Тест регистрации пройден: пользователь {} создан", testUsername);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.2.2 - Проверка валидации при регистрации (несовпадающие пароли)")
    @Description("Проверяет, что форма регистрации показывает ошибку при несовпадающих паролях")
    public void testRegistrationPasswordMismatch() throws InterruptedException {
        Allure.step("1. Переход на страницу регистрации");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        Allure.step("2. Заполнение формы с несовпадающими паролями");
        registrationPage.enterUsername(testUsername);
        registrationPage.enterEmail(testEmail);
        registrationPage.enterPassword(TEST_PASSWORD);
        registrationPage.enterConfirmPassword("WrongPassword123!");

        Allure.step("3. Отправка формы");
        registrationPage.submitRegistration();
        Thread.sleep(1000);

        Allure.step("4. Проверка сообщения об ошибке");
        boolean hasError = registrationPage.hasPasswordMismatchError();
        assertTrue(hasError, "Должно отображаться сообщение о несовпадении паролей");

        // Проверяем, что остались на странице регистрации
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/registration"),
                "При ошибке валидации должна оставаться страница регистрации");

        Allure.addAttachment("Статус", "✅ Валидация паролей работает корректно");
        log.info("✅ Тест валидации паролей пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.2.3 - Проверка валидации при регистрации (короткий логин)")
    @Description("Проверяет, что форма регистрации показывает ошибку при слишком коротком логине")
    public void testRegistrationShortLogin() throws InterruptedException {
        Allure.step("1. Переход на страницу регистрации");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        Allure.step("2. Заполнение формы с коротким логином");
        registrationPage.enterUsername(TEST_LOGIN_SHORT);
        registrationPage.enterEmail(testEmail);
        registrationPage.enterPassword(TEST_PASSWORD);
        registrationPage.enterConfirmPassword(TEST_PASSWORD);

        Allure.step("3. Отправка формы");
        registrationPage.submitRegistration();
        Thread.sleep(1000);

        Allure.step("4. Проверка сообщения об ошибке");
        boolean hasError = registrationPage.hasValidationError();
        assertTrue(hasError, "Должно отображаться сообщение о недопустимой длине логина");

        Allure.addAttachment("Статус", "✅ Валидация длины логина работает корректно");
        log.info("✅ Тест валидации логина пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.2.4 - Проверка валидации при регистрации (некорректный email)")
    @Description("Проверяет, что форма регистрации показывает ошибку при некорректном email")
    public void testRegistrationInvalidEmail() throws InterruptedException {
        Allure.step("1. Переход на страницу регистрации");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        Allure.step("2. Заполнение формы с некорректным email");
        registrationPage.enterUsername(testUsername);
        registrationPage.enterEmail(TEST_EMAIL_INVALID);
        registrationPage.enterPassword(TEST_PASSWORD);
        registrationPage.enterConfirmPassword(TEST_PASSWORD);

        Allure.step("3. Отправка формы");
        registrationPage.submitRegistration();
        Thread.sleep(1000);

        Allure.step("4. Проверка сообщения об ошибке");
        boolean hasError = registrationPage.hasValidationError();
        assertTrue(hasError, "Должно отображаться сообщение о некорректном email");

        Allure.addAttachment("Статус", "✅ Валидация email работает корректно");
        log.info("✅ Тест валидации email пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.2.5 - Проверка валидации при регистрации (слабый пароль)")
    @Description("Проверяет, что форма регистрации показывает ошибку при слишком коротком пароле")
    public void testRegistrationWeakPassword() throws InterruptedException {
        Allure.step("1. Переход на страницу регистрации");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        Allure.step("2. Заполнение формы со слабым паролем");
        registrationPage.enterUsername(testUsername);
        registrationPage.enterEmail(testEmail);
        registrationPage.enterPassword(TEST_PASSWORD_SHORT);
        registrationPage.enterConfirmPassword(TEST_PASSWORD_SHORT);

        Allure.step("3. Отправка формы");
        registrationPage.submitRegistration();
        Thread.sleep(1000);

        Allure.step("4. Проверка сообщения об ошибке");
        boolean hasError = registrationPage.hasValidationError();
        assertTrue(hasError, "Должно отображаться сообщение о недостаточной длине пароля");

        Allure.addAttachment("Статус", "✅ Валидация пароля работает корректно");
        log.info("✅ Тест валидации пароля пройден");
    }

    // ==================== ТЕСТЫ ВХОДА ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("6.2.6 - Полный цикл: вход в систему и выход")
    @Description("Проверяет полный цикл входа в систему с зарегистрированным пользователем и выход")
    public void testUserLoginFullCycle() throws InterruptedException {
        // ========== ШАГ 1: Сначала регистрируем пользователя ==========
        Allure.step("1. Регистрация нового пользователя");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        registrationPage.enterUsername(testUsername);
        registrationPage.enterEmail(testEmail);
        registrationPage.enterPassword(TEST_PASSWORD);
        registrationPage.enterConfirmPassword(TEST_PASSWORD);
        registrationPage.submitRegistration();
        Thread.sleep(1500);

        // ========== ШАГ 2: Вход в систему ==========
        Allure.step("2. Вход в систему с зарегистрированными данными");
        loginPage.enterUsername(testUsername);
        loginPage.enterPassword(TEST_PASSWORD);
        loginPage.submitLogin();
        Thread.sleep(1500);

        // ========== ШАГ 3: Проверка успешного входа ==========
        Allure.step("3. Проверка успешного входа");
        boolean isLoggedIn = loginPage.isLoggedIn();
        assertTrue(isLoggedIn, "Пользователь должен быть авторизован");

        String currentUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.contains("/login"), "Не должен быть на странице логина");

        // Проверяем, что отображается имя пользователя
        boolean usernameDisplayed = homePage.isUsernameDisplayed(testUsername);
        Allure.addAttachment("Отображение имени пользователя", String.valueOf(usernameDisplayed));

        // ========== ШАГ 4: Выход из системы ==========
        Allure.step("4. Выход из системы");
        loginPage.logout();
        Thread.sleep(1500);

        // ========== ШАГ 5: Проверка выхода ==========
        Allure.step("5. Проверка выхода из системы");
        boolean isLoggedOut = loginPage.isLoggedOut();
        assertTrue(isLoggedOut, "Пользователь должен быть разлогинен");

        Allure.addAttachment("Авторизованный пользователь", testUsername);
        Allure.addAttachment("Статус", "✅ Полный цикл входа/выхода пройден успешно");
        log.info("✅ Тест входа/выхода пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.2.7 - Проверка входа с неверным паролем")
    @Description("Проверяет, что при вводе неверного пароля отображается сообщение об ошибке")
    public void testLoginWrongPassword() throws InterruptedException {
        // ========== ШАГ 1: Регистрация пользователя ==========
        Allure.step("1. Регистрация нового пользователя");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        registrationPage.enterUsername(testUsername);
        registrationPage.enterEmail(testEmail);
        registrationPage.enterPassword(TEST_PASSWORD);
        registrationPage.enterConfirmPassword(TEST_PASSWORD);
        registrationPage.submitRegistration();
        Thread.sleep(1500);

        // ========== ШАГ 2: Попытка входа с неверным паролем ==========
        Allure.step("2. Попытка входа с неверным паролем");
        loginPage.enterUsername(testUsername);
        loginPage.enterPassword("WrongPassword123!");
        loginPage.submitLogin();
        Thread.sleep(1500);

        // ========== ШАГ 3: Проверка сообщения об ошибке ==========
        Allure.step("3. Проверка сообщения об ошибке");
        boolean hasError = loginPage.hasErrorMessage();
        assertTrue(hasError, "Должно отображаться сообщение об ошибке авторизации");

        // Проверяем, что остались на странице логина
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"), "Должна оставаться страница логина");

        Allure.addAttachment("Статус", "✅ Ошибка авторизации обрабатывается корректно");
        log.info("✅ Тест неверного пароля пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.2.8 - Проверка входа с несуществующим пользователем")
    @Description("Проверяет, что при вводе несуществующего логина отображается сообщение об ошибке")
    public void testLoginNonExistentUser() throws InterruptedException {
        Allure.step("1. Попытка входа с несуществующим пользователем");
        navigateTo(LOGIN_URL);
        loginPage.waitForPageLoad();

        loginPage.enterUsername("nonexistent_user_" + System.currentTimeMillis());
        loginPage.enterPassword(TEST_PASSWORD);
        loginPage.submitLogin();
        Thread.sleep(1500);

        Allure.step("2. Проверка сообщения об ошибке");
        boolean hasError = loginPage.hasErrorMessage();
        assertTrue(hasError, "Должно отображаться сообщение об ошибке авторизации");

        Allure.addAttachment("Статус", "✅ Несуществующий пользователь не может войти");
        log.info("✅ Тест несуществующего пользователя пройден");
    }

    // ==================== ТЕСТЫ ОТЗЫВОВ ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("6.2.9 - Полный цикл: регистрация → вход → добавление отзыва")
    @Description("Проверяет полный цикл: регистрация пользователя, вход, добавление отзыва и проверка отображения")
    public void testUserReviewFullCycle() throws InterruptedException {
        String testComment = "Это отличная тату-студия! Очень доволен работой мастера. E2E тест: " +
                UUID.randomUUID().toString().substring(0, 6);

        // ========== ШАГ 1: Регистрация пользователя ==========
        Allure.step("1. Регистрация нового пользователя");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        registrationPage.enterUsername(testUsername);
        registrationPage.enterEmail(testEmail);
        registrationPage.enterPassword(TEST_PASSWORD);
        registrationPage.enterConfirmPassword(TEST_PASSWORD);
        registrationPage.submitRegistration();
        Thread.sleep(1500);

        // ========== ШАГ 2: Вход в систему ==========
        Allure.step("2. Вход в систему");
        loginPage.enterUsername(testUsername);
        loginPage.enterPassword(TEST_PASSWORD);
        loginPage.submitLogin();
        Thread.sleep(1500);

        // ========== ШАГ 3: Переход на страницу отзывов ==========
        Allure.step("3. Переход на страницу отзывов");
        navigateTo(REVIEWS_URL);
        reviewsPage.waitForPageLoad();
        Thread.sleep(1000);

        int initialReviewsCount = reviewsPage.getReviewsCount();
        log.info("Начальное количество отзывов: {}", initialReviewsCount);
        Allure.addAttachment("Начальное количество отзывов", String.valueOf(initialReviewsCount));

        // ========== ШАГ 4: Добавление отзыва ==========
        Allure.step("4. Добавление нового отзыва");
        reviewsPage.addReview(testComment);
        Thread.sleep(2000);

        // ========== ШАГ 5: Проверка добавления отзыва ==========
        Allure.step("5. Проверка добавления отзыва");
        int afterAddReviewsCount = reviewsPage.getReviewsCount();

        if (afterAddReviewsCount > initialReviewsCount) {
            Allure.addAttachment("Результат", "Отзыв добавлен успешно");
            log.info("Количество отзывов увеличилось: {} → {}", initialReviewsCount, afterAddReviewsCount);
        }

        // Проверка наличия текста отзыва на странице
        boolean isCommentVisible = reviewsPage.isReviewCommentVisible(testComment);
        assertTrue(isCommentVisible, "Добавленный отзыв должен отображаться на странице");

        // ========== ШАГ 6: Проверка отображения отзыва на главной странице ==========
        Allure.step("6. Проверка отображения отзыва на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        boolean isReviewOnHomePage = homePage.isReviewVisible(testComment);
        Allure.addAttachment("Отзыв на главной", String.valueOf(isReviewOnHomePage));

        Allure.addAttachment("Текст отзыва", testComment);
        Allure.addAttachment("Автор отзыва", testUsername);
        Allure.addAttachment("Статус", "✅ Полный цикл регистрация→вход→отзыв пройден успешно");

        log.info("✅ Тест полного цикла пользователя пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.2.10 - Проверка добавления отзыва без авторизации")
    @Description("Проверяет, что неавторизованный пользователь не может добавить отзыв")
    public void testAddReviewWithoutLogin() throws InterruptedException {
        String testComment = "Этот отзыв должен быть невозможен без авторизации";

        Allure.step("1. Переход на страницу отзывов без авторизации");
        navigateTo(REVIEWS_URL);
        reviewsPage.waitForPageLoad();
        Thread.sleep(1000);

        Allure.step("2. Попытка добавить отзыв");
        // Проверяем, что форма отзыва недоступна или требует авторизации
        boolean isReviewFormAvailable = reviewsPage.isReviewFormAvailable();

        if (isReviewFormAvailable) {
            reviewsPage.addReview(testComment);
            Thread.sleep(2000);

            // Проверяем, что отзыв не добавился или произошел редирект на логин
            boolean isReviewVisible = reviewsPage.isReviewCommentVisible(testComment);
            assertFalse(isReviewVisible, "Неавторизованный пользователь не должен добавлять отзывы");

            String currentUrl = driver.getCurrentUrl();
            boolean isRedirectedToLogin = currentUrl.contains("/login");
            Allure.addAttachment("Перенаправление на логин", String.valueOf(isRedirectedToLogin));
        } else {
            Allure.addAttachment("Статус", "Форма отзыва недоступна для неавторизованных пользователей");
        }

        log.info("✅ Тест добавления отзыва без авторизации пройден");
    }

    // ==================== ТЕСТЫ ЛИЧНОГО КАБИНЕТА ====================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.2.11 - Проверка доступа к личному кабинету")
    @Description("Проверяет, что авторизованный пользователь может получить доступ к личному кабинету")
    public void testAccessToPersonalCabinet() throws InterruptedException {
        // ========== ШАГ 1: Регистрация пользователя ==========
        Allure.step("1. Регистрация нового пользователя");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        registrationPage.enterUsername(testUsername);
        registrationPage.enterEmail(testEmail);
        registrationPage.enterPassword(TEST_PASSWORD);
        registrationPage.enterConfirmPassword(TEST_PASSWORD);
        registrationPage.submitRegistration();
        Thread.sleep(1500);

        // ========== ШАГ 2: Вход в систему ==========
        Allure.step("2. Вход в систему");
        loginPage.enterUsername(testUsername);
        loginPage.enterPassword(TEST_PASSWORD);
        loginPage.submitLogin();
        Thread.sleep(1500);

        // ========== ШАГ 3: Переход в личный кабинет ==========
        Allure.step("3. Переход в личный кабинет");
        navigateTo(LK_URL);
        lkPage.waitForPageLoad();
        Thread.sleep(1000);

        // ========== ШАГ 4: Проверка отображения данных пользователя ==========
        Allure.step("4. Проверка отображения данных пользователя");
        boolean usernameDisplayed = lkPage.isUsernameDisplayed(testUsername);
        boolean emailDisplayed = lkPage.isEmailDisplayed(testEmail);

        assertTrue(usernameDisplayed, "Имя пользователя должно отображаться в личном кабинете");
        assertTrue(emailDisplayed, "Email должен отображаться в личном кабинете");

        Allure.addAttachment("Отображение имени", String.valueOf(usernameDisplayed));
        Allure.addAttachment("Отображение email", String.valueOf(emailDisplayed));
        Allure.addAttachment("Статус", "✅ Личный кабинет доступен и отображает данные пользователя");

        log.info("✅ Тест доступа к личному кабинету пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.2.12 - Проверка доступа к личному кабинету без авторизации")
    @Description("Проверяет, что неавторизованный пользователь не может получить доступ к личному кабинету")
    public void testAccessToPersonalCabinetWithoutLogin() throws InterruptedException {
        Allure.step("1. Попытка перехода в личный кабинет без авторизации");
        navigateTo(LK_URL);
        Thread.sleep(1500);

        Allure.step("2. Проверка перенаправления на страницу логина");
        String currentUrl = driver.getCurrentUrl();
        boolean isRedirectedToLogin = currentUrl.contains("/login");

        assertTrue(isRedirectedToLogin,
                "Неавторизованный пользователь должен быть перенаправлен на страницу логина. Текущий URL: " + currentUrl);

        Allure.addAttachment("URL после перехода", currentUrl);
        Allure.addAttachment("Статус", "✅ Доступ к личному кабинету защищен");

        log.info("✅ Тест защиты личного кабинета пройден");
    }

    // ==================== ТЕСТЫ ССЫЛОК ====================

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("6.2.13 - Проверка ссылки на регистрацию со страницы логина")
    @Description("Проверяет, что ссылка на регистрацию со страницы логина ведет на страницу регистрации")
    public void testRegistrationLinkFromLogin() throws InterruptedException {
        Allure.step("1. Переход на страницу логина");
        navigateTo(LOGIN_URL);
        loginPage.waitForPageLoad();

        Allure.step("2. Нажатие на ссылку регистрации");
        loginPage.clickRegistrationLink();
        Thread.sleep(1500);

        Allure.step("3. Проверка перехода на страницу регистрации");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/registration"),
                "Должен быть переход на страницу регистрации. Текущий URL: " + currentUrl);

        boolean isRegistrationPageLoaded = registrationPage.isRegistrationPageLoaded();
        assertTrue(isRegistrationPageLoaded, "Страница регистрации должна загрузиться");

        Allure.addAttachment("Статус", "✅ Ссылка на регистрацию работает корректно");
        log.info("✅ Тест ссылки на регистрацию пройден");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("6.2.14 - Проверка ссылки на вход со страницы регистрации")
    @Description("Проверяет, что ссылка на вход со страницы регистрации ведет на страницу логина")
    public void testLoginLinkFromRegistration() throws InterruptedException {
        Allure.step("1. Переход на страницу регистрации");
        navigateTo(REGISTRATION_URL);
        registrationPage.waitForPageLoad();

        Allure.step("2. Нажатие на ссылку входа");
        registrationPage.clickLoginLink();
        Thread.sleep(1500);

        Allure.step("3. Проверка перехода на страницу логина");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "Должен быть переход на страницу логина. Текущий URL: " + currentUrl);

        boolean isLoginPageLoaded = loginPage.isLoginPageLoaded();
        assertTrue(isLoginPageLoaded, "Страница логина должна загрузиться");

        Allure.addAttachment("Статус", "✅ Ссылка на вход работает корректно");
        log.info("✅ Тест ссылки на вход пройден");
    }
}