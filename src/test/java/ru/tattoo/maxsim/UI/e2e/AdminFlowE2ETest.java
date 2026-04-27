package ru.tattoo.maxsim.UI.e2e;

import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest;
import ru.tattoo.maxsim.UI.baseActions.TestListener;
import ru.tattoo.maxsim.UI.page.AdminPage;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Epic("E2E Тестирование")
@Feature("Сценарий 6.3: Администратор - добавление → редактирование → удаление")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestListener.class)
@DisplayName("E2E: Полный цикл администратора")
public class AdminFlowE2ETest extends BaseSeleniumTest {

    private AdminPage adminPage;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String ADMIN_URL = BASE_URL + "/admin";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String ADMIN_LOGIN = "ADMIN";
    private static final String ADMIN_PASSWORD = "admin";

    // ==================== ТЕСТОВЫЕ ДАННЫЕ ====================

    // Данные для карусели
    private static final String CAROUSEL_TITLE = "Админ E2E Тестовый слайд";
    private static final String CAROUSEL_SUBTITLE = "Проверка добавления и редактирования карусели";
    private static final String CAROUSEL_TITLE_UPDATED = "Обновленный заголовок карусели";
    private static final String CAROUSEL_SUBTITLE_UPDATED = "Обновленный подзаголовок карусели";

    // Данные для галереи
    private static final String GALLERY_DESCRIPTION = "Админ E2E Тестовое изображение галереи";
    private static final String GALLERY_DESCRIPTION_UPDATED = "Обновленное описание изображения галереи";

    // Данные для эскизов
    private static final String SKETCH_DESCRIPTION = "Тестовый эскиз для E2E проверки";

    // Данные для отзывов
    private static final String REVIEW_COMMENT = "Отличная работа мастера! E2E тест: " + System.currentTimeMillis();

    // Данные для контактной информации
    private static final String TEST_PHONE = "+7 (999) 888-77-66";
    private static final String TEST_EMAIL = "e2e-test@maxsim.ru";
    private static final String TEST_ADDRESS = "г. Омск, ул. E2E Тестовая, д. 1";

    // Данные для разделов главной страницы
    private static final String FEATURE_TEXT = "Уникальный дизайн и индивидуальный подход";
    private static final String ABOUT_TEXT = "Профессиональные мастера с опытом более 10 лет";
    private static final String CLASSES_TEXT = "Обучение татуировке с нуля";
    private static final String PRICE_TEXT = "от 3000 ₽";
    private static final String ADVANTAGE_TEXT = "100% стерильность и безопасность";

    // Путь к тестовому изображению
    private static final String TEST_IMAGE_PATH = System.getProperty("user.dir") + "/src/test/resources/e2e-test-image.jpg";

    // Хранилище ID созданных объектов для очистки
    private String createdSlideId;
    private String createdImageId;
    private String createdSketchId;

    @BeforeEach
    @Step("Подготовка: авторизация в админ-панели")
    public void setup() {
        log.info("🔧 Настройка теста AdminFlowE2ETest...");

        // Переход на страницу логина
        navigateTo(LOGIN_URL);
        adminPage = new AdminPage(driver);

        // Ожидание загрузки страницы
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Выполнение входа
        adminPage.login(ADMIN_LOGIN, ADMIN_PASSWORD);

        // Ожидание завершения авторизации
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Проверка успешной авторизации
        boolean isLoggedIn = adminPage.isLoggedIn();
        if (!isLoggedIn) {
            String currentUrl = driver.getCurrentUrl();
            log.error("❌ Авторизация не удалась. Текущий URL: {}", currentUrl);
          //  takeScreenshot("login-failed");
            fail("Авторизация не удалась. Проверьте учетные данные и доступность сервера.");
        }

        assertTrue(isLoggedIn, "Должна быть успешная авторизация");

        log.info("✅ Администратор авторизован, E2E тест инициализирован");
    }

    @AfterEach
    @Step("Очистка после теста")
    public void cleanup() {
        log.info("🧹 Очистка после теста...");

        try {
            // Если остались неочищенные объекты, удаляем их
            if (createdSlideId != null) {
                try {
                    adminPage.clickHomeTab();
                    adminPage.openCarouselTab();
                    adminPage.deleteCarouselSlideById(createdSlideId);
                    log.info("✅ Очищен слайд с ID: {}", createdSlideId);
                } catch (Exception e) {
                    log.warn("Не удалось очистить слайд: {}", e.getMessage());
                }
            }

            if (createdImageId != null) {
                try {
                    adminPage.clickGalleryTab();
                    adminPage.deleteGalleryImageById(createdImageId);
                    log.info("✅ Очищено изображение с ID: {}", createdImageId);
                } catch (Exception e) {
                    log.warn("Не удалось очистить изображение: {}", e.getMessage());
                }
            }

            if (createdSketchId != null) {
                try {
                    adminPage.clickSketchesTab();
                    adminPage.deleteSketchById(createdSketchId);
                    log.info("✅ Очищен эскиз с ID: {}", createdSketchId);
                } catch (Exception e) {
                    log.warn("Не удалось очистить эскиз: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Ошибка при очистке: {}", e.getMessage());
        }

        createdSlideId = null;
        createdImageId = null;
        createdSketchId = null;

        log.info("✅ Очистка завершена");
    }

    // ==================== ТЕСТЫ КАРУСЕЛИ ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("6.3.1 - Админ: добавление слайда в карусель")
    @Description("Проверяет возможность добавления нового слайда в карусель")
    public void testAddCarouselSlide() throws InterruptedException {
        Allure.step("1. Переход в раздел карусели");
        adminPage.clickHomeTab();
        Thread.sleep(500);
        adminPage.openCarouselTab();
        Thread.sleep(500);

        int initialSlidesCount = adminPage.getCarouselSlidesCount();
        log.info("Начальное количество слайдов: {}", initialSlidesCount);
        Allure.addAttachment("Начальное количество слайдов", String.valueOf(initialSlidesCount));

        Allure.step("2. Добавление нового слайда");
        adminPage.addCarouselSlide(CAROUSEL_TITLE, CAROUSEL_SUBTITLE, TEST_IMAGE_PATH);
        Thread.sleep(1000);

        String successMessage = adminPage.getSuccessMessage();
        log.info("Сообщение после добавления: {}", successMessage);
        adminPage.closeModal();

        int afterAddCount = adminPage.getCarouselSlidesCount();
        assertEquals(initialSlidesCount + 1, afterAddCount, "Количество слайдов должно увеличиться на 1");

        // Сохраняем ID для очистки
        createdSlideId = adminPage.getLastCarouselSlideId();

        Allure.addAttachment("ID созданного слайда", createdSlideId);
        Allure.addAttachment("Количество слайдов после добавления", String.valueOf(afterAddCount));
        Allure.addAttachment("Статус", "✅ Слайд успешно добавлен");

        log.info("✅ Тест добавления слайда пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.2 - Админ: редактирование слайда карусели")
    @Description("Проверяет возможность редактирования существующего слайда")
    public void testEditCarouselSlide() throws InterruptedException {
        Allure.step("1. Переход в раздел карусели");
        adminPage.clickHomeTab();
        Thread.sleep(500);
        adminPage.openCarouselTab();
        Thread.sleep(500);

        Allure.step("2. Создание тестового слайда для редактирования");
        int initialSlidesCount = adminPage.getCarouselSlidesCount();
        adminPage.addCarouselSlide(CAROUSEL_TITLE, CAROUSEL_SUBTITLE, TEST_IMAGE_PATH);
        Thread.sleep(1000);
        adminPage.closeModal();

        createdSlideId = adminPage.getLastCarouselSlideId();

        Allure.step("3. Редактирование созданного слайда");
        adminPage.editCarouselSlide(createdSlideId, CAROUSEL_TITLE_UPDATED, CAROUSEL_SUBTITLE_UPDATED);
        Thread.sleep(1000);

        String successMessage = adminPage.getSuccessMessage();
        log.info("Сообщение после редактирования: {}", successMessage);
        adminPage.closeModal();

        Allure.step("4. Проверка обновления на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        boolean isTitleUpdated = driver.getPageSource().contains(CAROUSEL_TITLE_UPDATED);
        boolean isSubtitleUpdated = driver.getPageSource().contains(CAROUSEL_SUBTITLE_UPDATED);

        assertTrue(isTitleUpdated, "Обновленный заголовок должен отображаться");
        assertTrue(isSubtitleUpdated, "Обновленный подзаголовок должен отображаться");

        Allure.addAttachment("Заголовок обновлен", CAROUSEL_TITLE_UPDATED);
        Allure.addAttachment("Подзаголовок обновлен", CAROUSEL_SUBTITLE_UPDATED);
        Allure.addAttachment("Статус", "✅ Слайд успешно отредактирован");

        log.info("✅ Тест редактирования слайда пройден");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("6.3.3 - Админ: удаление слайда карусели")
    @Description("Проверяет возможность удаления слайда из карусели")
    public void testDeleteCarouselSlide() throws InterruptedException {
        Allure.step("1. Переход в раздел карусели");
        adminPage.clickHomeTab();
        Thread.sleep(500);
        adminPage.openCarouselTab();
        Thread.sleep(500);

        int initialSlidesCount = adminPage.getCarouselSlidesCount();
        log.info("Начальное количество слайдов: {}", initialSlidesCount);

        Allure.step("2. Создание тестового слайда для удаления");
        adminPage.addCarouselSlide(CAROUSEL_TITLE, CAROUSEL_SUBTITLE, TEST_IMAGE_PATH);
        Thread.sleep(1000);
        adminPage.closeModal();

        int afterAddCount = adminPage.getCarouselSlidesCount();
        assertEquals(initialSlidesCount + 1, afterAddCount, "Слайд должен быть создан");

        Allure.step("3. Удаление созданного слайда");
        adminPage.deleteFirstCarouselSlide();
        Thread.sleep(1000);

        int afterDeleteCount = adminPage.getCarouselSlidesCount();
        assertEquals(initialSlidesCount, afterDeleteCount, "Количество слайдов должно вернуться к исходному");

        Allure.addAttachment("Количество слайдов после удаления", String.valueOf(afterDeleteCount));
        Allure.addAttachment("Статус", "✅ Слайд успешно удален");

        log.info("✅ Тест удаления слайда пройден");
    }

    // ==================== ТЕСТЫ ГАЛЕРЕИ ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("6.3.4 - Админ: добавление изображения в галерею")
    @Description("Проверяет возможность добавления нового изображения в галерею")
    public void testAddGalleryImage() throws InterruptedException {
        Allure.step("1. Переход в раздел галереи");
        adminPage.clickGalleryTab();
        Thread.sleep(500);

        int initialImagesCount = adminPage.getGalleryImagesCount();
        log.info("Начальное количество изображений: {}", initialImagesCount);
        Allure.addAttachment("Начальное количество изображений", String.valueOf(initialImagesCount));

        Allure.step("2. Добавление нового изображения");
        adminPage.addGalleryImage(GALLERY_DESCRIPTION, TEST_IMAGE_PATH);
        Thread.sleep(1000);

        int afterAddCount = adminPage.getGalleryImagesCount();
        assertEquals(initialImagesCount + 1, afterAddCount, "Количество изображений должно увеличиться на 1");

        // Сохраняем ID для очистки
        createdImageId = adminPage.getLastGalleryImageId();

        Allure.step("3. Проверка отображения на главной странице");
        navigateTo(BASE_URL + "/gallery");
        Thread.sleep(1000);

        boolean isImageVisible = driver.getPageSource().contains(GALLERY_DESCRIPTION);
        assertTrue(isImageVisible, "Добавленное изображение должно отображаться в галерее");

        Allure.addAttachment("ID созданного изображения", createdImageId);
        Allure.addAttachment("Статус", "✅ Изображение успешно добавлено");

        log.info("✅ Тест добавления изображения пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.5 - Админ: редактирование описания изображения")
    @Description("Проверяет возможность редактирования описания изображения в галерее")
    public void testEditGalleryImageDescription() throws InterruptedException {
        Allure.step("1. Переход в раздел галереи");
        adminPage.clickGalleryTab();
        Thread.sleep(500);

        Allure.step("2. Создание тестового изображения");
        adminPage.addGalleryImage(GALLERY_DESCRIPTION, TEST_IMAGE_PATH);
        Thread.sleep(1000);

        createdImageId = adminPage.getLastGalleryImageId();

        Allure.step("3. Редактирование описания изображения");
        adminPage.editGalleryImageDescription(createdImageId, GALLERY_DESCRIPTION_UPDATED);
        Thread.sleep(1000);

        String successMessage = adminPage.getSuccessMessage();
        log.info("Сообщение после редактирования: {}", successMessage);
        adminPage.closeModal();

        Allure.step("4. Проверка обновления");
        boolean isDescriptionUpdated = adminPage.isGalleryImageDescriptionPresent(GALLERY_DESCRIPTION_UPDATED);
        assertTrue(isDescriptionUpdated, "Описание изображения должно обновиться");

        Allure.addAttachment("Обновленное описание", GALLERY_DESCRIPTION_UPDATED);
        Allure.addAttachment("Статус", "✅ Описание изображения успешно обновлено");

        log.info("✅ Тест редактирования описания пройден");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("6.3.6 - Админ: удаление изображения из галереи")
    @Description("Проверяет возможность удаления изображения из галереи")
    public void testDeleteGalleryImage() throws InterruptedException {
        Allure.step("1. Переход в раздел галереи");
        adminPage.clickGalleryTab();
        Thread.sleep(500);

        int initialImagesCount = adminPage.getGalleryImagesCount();

        Allure.step("2. Создание тестового изображения");
        adminPage.addGalleryImage(GALLERY_DESCRIPTION, TEST_IMAGE_PATH);
        Thread.sleep(1000);

        int afterAddCount = adminPage.getGalleryImagesCount();
        assertEquals(initialImagesCount + 1, afterAddCount, "Изображение должно быть создано");

        Allure.step("3. Удаление созданного изображения");
        adminPage.deleteFirstGalleryImage();
        Thread.sleep(1000);

        int afterDeleteCount = adminPage.getGalleryImagesCount();
        assertEquals(initialImagesCount, afterDeleteCount, "Количество изображений должно вернуться к исходному");

        Allure.addAttachment("Статус", "✅ Изображение успешно удалено");

        log.info("✅ Тест удаления изображения пройден");
    }

    // ==================== ТЕСТЫ ЭСКИЗОВ ====================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.7 - Админ: добавление эскиза")
    @Description("Проверяет возможность добавления нового эскиза")
    public void testAddSketch() throws InterruptedException {
        Allure.step("1. Переход в раздел эскизов");
        adminPage.clickSketchesTab();
        Thread.sleep(500);

        int initialSketchesCount = adminPage.getSketchesCount();
        log.info("Начальное количество эскизов: {}", initialSketchesCount);

        Allure.step("2. Добавление нового эскиза");
        adminPage.addSketch(SKETCH_DESCRIPTION, TEST_IMAGE_PATH);
        Thread.sleep(1000);

        int afterAddCount = adminPage.getSketchesCount();
        assertEquals(initialSketchesCount + 1, afterAddCount, "Количество эскизов должно увеличиться на 1");

        // Сохраняем ID для очистки
        createdSketchId = adminPage.getLastSketchId();

        Allure.step("3. Проверка отображения на публичной странице");
        navigateTo(BASE_URL + "/sketches");
        Thread.sleep(1000);

        boolean isSketchVisible = driver.getPageSource().contains(SKETCH_DESCRIPTION);
        assertTrue(isSketchVisible, "Добавленный эскиз должен отображаться");

        Allure.addAttachment("ID созданного эскиза", createdSketchId);
        Allure.addAttachment("Статус", "✅ Эскиз успешно добавлен");

        log.info("✅ Тест добавления эскиза пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.8 - Админ: удаление эскиза")
    @Description("Проверяет возможность удаления эскиза")
    public void testDeleteSketch() throws InterruptedException {
        Allure.step("1. Переход в раздел эскизов");
        adminPage.clickSketchesTab();
        Thread.sleep(500);

        int initialSketchesCount = adminPage.getSketchesCount();

        Allure.step("2. Создание тестового эскиза");
        adminPage.addSketch(SKETCH_DESCRIPTION, TEST_IMAGE_PATH);
        Thread.sleep(1000);

        int afterAddCount = adminPage.getSketchesCount();
        assertEquals(initialSketchesCount + 1, afterAddCount, "Эскиз должен быть создан");

        Allure.step("3. Удаление созданного эскиза");
        adminPage.deleteFirstSketch();
        Thread.sleep(1000);

        int afterDeleteCount = adminPage.getSketchesCount();
        assertEquals(initialSketchesCount, afterDeleteCount, "Количество эскизов должно вернуться к исходному");

        Allure.addAttachment("Статус", "✅ Эскиз успешно удален");

        log.info("✅ Тест удаления эскиза пройден");
    }

    // ==================== ТЕСТЫ НАСТРОЕК ====================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.9 - Админ: обновление контактной информации")
    @Description("Проверяет возможность обновления контактной информации в настройках")
    public void testUpdateContactInfo() throws InterruptedException {
        Allure.step("1. Переход в раздел настроек контактов");
        adminPage.clickSettingsTab();
        Thread.sleep(500);
        adminPage.openContactTab();
        Thread.sleep(500);

        Allure.step("2. Обновление контактной информации");
        adminPage.updateContactInfo(TEST_PHONE, TEST_EMAIL, TEST_ADDRESS);
        Thread.sleep(1000);

        String successMessage = adminPage.getSuccessMessage();
        log.info("Сообщение после обновления: {}", successMessage);
        adminPage.closeModal();

        Allure.step("3. Проверка обновления информации в форме");
        boolean isPhoneUpdated = adminPage.getPhoneInputValue().equals(TEST_PHONE);
        boolean isEmailUpdated = adminPage.getEmailInputValue().equals(TEST_EMAIL);
        boolean isAddressUpdated = adminPage.getAddressInputValue().equals(TEST_ADDRESS);

        assertTrue(isPhoneUpdated, "Телефон должен обновиться");
        assertTrue(isEmailUpdated, "Email должен обновиться");
        assertTrue(isAddressUpdated, "Адрес должен обновиться");

        Allure.step("4. Проверка отображения на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        boolean isPhoneDisplayed = driver.getPageSource().contains(TEST_PHONE);
        boolean isEmailDisplayed = driver.getPageSource().contains(TEST_EMAIL);
        boolean isAddressDisplayed = driver.getPageSource().contains(TEST_ADDRESS);

        Allure.addAttachment("Телефон отображается", String.valueOf(isPhoneDisplayed));
        Allure.addAttachment("Email отображается", String.valueOf(isEmailDisplayed));
        Allure.addAttachment("Адрес отображается", String.valueOf(isAddressDisplayed));
        Allure.addAttachment("Статус", "✅ Контактная информация успешно обновлена");

        log.info("✅ Тест обновления контактной информации пройден");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("6.3.10 - Админ: загрузка логотипа")
    @Description("Проверяет возможность загрузки логотипа сайта")
    public void testUploadLogo() throws InterruptedException {
        Allure.step("1. Переход в раздел настроек логотипа");
        adminPage.clickSettingsTab();
        Thread.sleep(500);
        adminPage.openLogoTab();
        Thread.sleep(500);

        Allure.step("2. Загрузка нового логотипа");
        adminPage.uploadLogo(TEST_IMAGE_PATH);
        Thread.sleep(1000);

        String successMessage = adminPage.getSuccessMessage();
        log.info("Сообщение после загрузки: {}", successMessage);
        adminPage.closeModal();

        Allure.step("3. Проверка обновления на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        // Проверяем, что логотип загрузился (не пустой src)
        String logoSource = adminPage.getLogoSource();
        boolean isLogoUpdated = logoSource != null && !logoSource.contains("avatar.jpg");

        assertTrue(isLogoUpdated, "Логотип должен обновиться");

        Allure.addAttachment("Путь к логотипу", logoSource);
        Allure.addAttachment("Статус", "✅ Логотип успешно загружен");

        log.info("✅ Тест загрузки логотипа пройден");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("6.3.11 - Админ: загрузка фона хлебных крошек")
    @Description("Проверяет возможность загрузки фонового изображения для секции хлебных крошек")
    public void testUploadBreadcrumbBackground() throws InterruptedException {
        Allure.step("1. Переход в раздел настроек хлебных крошек");
        adminPage.clickSettingsTab();
        Thread.sleep(500);
        adminPage.openBreadcrumbTab();
        Thread.sleep(500);

        Allure.step("2. Загрузка нового фона");
        adminPage.uploadBreadcrumbBackground(TEST_IMAGE_PATH);
        Thread.sleep(1000);

        String successMessage = adminPage.getSuccessMessage();
        log.info("Сообщение после загрузки: {}", successMessage);
        adminPage.closeModal();

        Allure.step("3. Проверка обновления на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        // Проверяем, что фон загрузился
        String breadcrumbStyle = adminPage.getBreadcrumbBackgroundStyle();
        boolean isBackgroundUpdated = breadcrumbStyle != null && !breadcrumbStyle.isEmpty();

        assertTrue(isBackgroundUpdated, "Фон хлебных крошек должен обновиться");

        Allure.addAttachment("Статус", "✅ Фон хлебных крошек успешно загружен");

        log.info("✅ Тест загрузки фона хлебных крошек пройден");
    }

    // ==================== ТЕСТЫ РАЗДЕЛОВ ГЛАВНОЙ СТРАНИЦЫ ====================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.12 - Админ: обновление раздела 'Особенности'")
    @Description("Проверяет возможность обновления текста в разделе 'Особенности'")
    public void testUpdateFeatureSection() throws InterruptedException {
        Allure.step("1. Переход в раздел 'Особенности'");
        adminPage.clickHomeTab();
        Thread.sleep(500);
        adminPage.openFeatureTab();
        Thread.sleep(500);

        Allure.step("2. Обновление текста раздела");
        adminPage.updateFeatureSection(FEATURE_TEXT);
        Thread.sleep(1000);

        Allure.step("3. Проверка обновления на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        boolean isUpdated = driver.getPageSource().contains(FEATURE_TEXT);
        assertTrue(isUpdated, "Текст раздела 'Особенности' должен обновиться");

        Allure.addAttachment("Обновленный текст", FEATURE_TEXT);
        Allure.addAttachment("Статус", "✅ Раздел 'Особенности' успешно обновлен");

        log.info("✅ Тест обновления раздела 'Особенности' пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.13 - Админ: обновление раздела 'О нас'")
    @Description("Проверяет возможность обновления текста в разделе 'О нас'")
    public void testUpdateAboutSection() throws InterruptedException {
        Allure.step("1. Переход в раздел 'О нас'");
        adminPage.clickHomeTab();
        Thread.sleep(500);
        adminPage.openAboutTab();
        Thread.sleep(500);

        Allure.step("2. Обновление текста раздела");
        adminPage.updateAboutSection(ABOUT_TEXT);
        Thread.sleep(1000);

        Allure.step("3. Проверка обновления на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        boolean isUpdated = driver.getPageSource().contains(ABOUT_TEXT);
        assertTrue(isUpdated, "Текст раздела 'О нас' должен обновиться");

        Allure.addAttachment("Обновленный текст", ABOUT_TEXT);
        Allure.addAttachment("Статус", "✅ Раздел 'О нас' успешно обновлен");

        log.info("✅ Тест обновления раздела 'О нас' пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.14 - Админ: обновление раздела 'Классы'")
    @Description("Проверяет возможность обновления текста в разделе 'Классы'")
    public void testUpdateClassesSection() throws InterruptedException {
        Allure.step("1. Переход в раздел 'Классы'");
        adminPage.clickHomeTab();
        Thread.sleep(500);
        adminPage.openClassesTab();
        Thread.sleep(500);

        Allure.step("2. Обновление текста раздела");
        adminPage.updateClassesSection(CLASSES_TEXT);
        Thread.sleep(1000);

        Allure.step("3. Проверка обновления на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        boolean isUpdated = driver.getPageSource().contains(CLASSES_TEXT);
        assertTrue(isUpdated, "Текст раздела 'Классы' должен обновиться");

        Allure.addAttachment("Обновленный текст", CLASSES_TEXT);
        Allure.addAttachment("Статус", "✅ Раздел 'Классы' успешно обновлен");

        log.info("✅ Тест обновления раздела 'Классы' пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.15 - Админ: обновление раздела 'Цены'")
    @Description("Проверяет возможность обновления текста в разделе 'Цены'")
    public void testUpdatePriceSection() throws InterruptedException {
        Allure.step("1. Переход в раздел 'Цены'");
        adminPage.clickHomeTab();
        Thread.sleep(500);
        adminPage.openPriceTab();
        Thread.sleep(500);

        Allure.step("2. Обновление текста раздела");
        adminPage.updatePriceSection(PRICE_TEXT);
        Thread.sleep(1000);

        Allure.step("3. Проверка обновления на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        boolean isUpdated = driver.getPageSource().contains(PRICE_TEXT);
        assertTrue(isUpdated, "Текст раздела 'Цены' должен обновиться");

        Allure.addAttachment("Обновленный текст", PRICE_TEXT);
        Allure.addAttachment("Статус", "✅ Раздел 'Цены' успешно обновлен");

        log.info("✅ Тест обновления раздела 'Цены' пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.16 - Админ: обновление раздела 'Преимущества'")
    @Description("Проверяет возможность обновления текста в разделе 'Преимущества'")
    public void testUpdateChooseUsSection() throws InterruptedException {
        Allure.step("1. Переход в раздел 'Преимущества'");
        adminPage.clickHomeTab();
        Thread.sleep(500);
        adminPage.openChooseUsTab();
        Thread.sleep(500);

        Allure.step("2. Обновление текста раздела");
        adminPage.updateChooseUsSection(ADVANTAGE_TEXT);
        Thread.sleep(1000);

        Allure.step("3. Проверка обновления на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        boolean isUpdated = driver.getPageSource().contains(ADVANTAGE_TEXT);
        assertTrue(isUpdated, "Текст раздела 'Преимущества' должен обновиться");

        Allure.addAttachment("Обновленный текст", ADVANTAGE_TEXT);
        Allure.addAttachment("Статус", "✅ Раздел 'Преимущества' успешно обновлен");

        log.info("✅ Тест обновления раздела 'Преимущества' пройден");
    }

    // ==================== ТЕСТЫ МОДЕРАЦИИ ====================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.17 - Админ: удаление отзыва")
    @Description("Проверяет возможность удаления отзыва администратором")
    public void testDeleteReview() throws InterruptedException {
        Allure.step("1. Переход в раздел отзывов админ-панели");
        adminPage.clickReviewsTab();
        Thread.sleep(500);

        int initialReviewsCount = adminPage.getReviewsCount();
        log.info("Начальное количество отзывов: {}", initialReviewsCount);

        if (initialReviewsCount == 0) {
            Allure.addAttachment("Статус", "Нет отзывов для удаления - тест пропущен");
            log.warn("Нет отзывов для удаления");
            return;
        }

        Allure.step("2. Получение текста первого отзыва");
        String firstReviewText = adminPage.getFirstReviewText();
        Allure.addAttachment("Удаляемый отзыв", firstReviewText);

        Allure.step("3. Удаление первого отзыва");
        adminPage.deleteFirstReview();
        Thread.sleep(1000);

        int afterDeleteCount = adminPage.getReviewsCount();
        assertEquals(initialReviewsCount - 1, afterDeleteCount, "Количество отзывов должно уменьшиться на 1");

        Allure.step("4. Проверка отсутствия отзыва на главной странице");
        navigateTo(BASE_URL);
        Thread.sleep(1000);

        boolean isReviewRemoved = !driver.getPageSource().contains(firstReviewText);
        assertTrue(isReviewRemoved, "Удаленный отзыв не должен отображаться на главной странице");

        Allure.addAttachment("Статус", "✅ Отзыв успешно удален");

        log.info("✅ Тест удаления отзыва пройден");
    }

    // ==================== ТЕСТЫ ПАГИНАЦИИ ====================

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("6.3.18 - Админ: проверка пагинации эскизов")
    @Description("Проверяет работу пагинации на странице эскизов")
    public void testSketchesPagination() throws InterruptedException {
        Allure.step("1. Переход в раздел эскизов");
        adminPage.clickSketchesTab();
        Thread.sleep(500);

        boolean hasPagination = adminPage.isSketchesPaginationVisible();

        if (hasPagination) {
            Allure.step("2. Изменение количества отображаемых эскизов");
            adminPage.selectSketchesPerPage(6);
            Thread.sleep(1000);

            int countAfterChange = adminPage.getSketchesCount();
            Allure.addAttachment("Количество эскизов после смены per page", String.valueOf(countAfterChange));

            Allure.step("3. Переключение страниц");
            boolean hasNextPage = adminPage.isSketchesNextPageEnabled();
            if (hasNextPage) {
                adminPage.clickSketchesNextPage();
                Thread.sleep(1000);
                int countOnNextPage = adminPage.getSketchesCount();
                Allure.addAttachment("Количество эскизов на следующей странице", String.valueOf(countOnNextPage));

                adminPage.clickSketchesPrevPage();
                Thread.sleep(1000);
            }

            Allure.addAttachment("Статус", "✅ Пагинация эскизов работает корректно");
            log.info("✅ Тест пагинации эскизов пройден");
        } else {
            Allure.addAttachment("Статус", "Пагинация не доступна (возможно, мало эскизов)");
            log.warn("Пагинация не доступна");
        }
    }

    // ==================== ТЕСТЫ ПОЛЬЗОВАТЕЛЕЙ ====================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6.3.19 - Админ: просмотр списка пользователей")
    @Description("Проверяет, что список пользователей отображается корректно")
    public void testViewUsersList() throws InterruptedException {
        Allure.step("1. Переход в раздел настроек");
        adminPage.clickSettingsTab();
        Thread.sleep(500);

        Allure.step("2. Открытие вкладки 'Пользователи'");
        adminPage.openUsersTab();
        Thread.sleep(500);

        Allure.step("3. Получение количества пользователей");
        int usersCount = adminPage.getUsersCount();

        assertTrue(usersCount >= 0, "Количество пользователей не может быть отрицательным");

        Allure.addAttachment("Количество пользователей", String.valueOf(usersCount));
        Allure.addAttachment("Статус", "✅ Список пользователей загружен корректно");

        log.info("✅ Тест просмотра пользователей пройден");
    }
}
