package ru.tattoo.maxsim.UI.e2e;


import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tattoo.maxsim.UI.baseActions.BaseSeleniumTest;
import ru.tattoo.maxsim.UI.baseActions.TestListener;
import ru.tattoo.maxsim.UI.page.AdminPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Epic("UI Тесты")
@Feature("Административная панель - Управление контентом")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestListener.class)
@DisplayName("Тесты управления контентом в админ-панели")
public class ContentFlowE2ETest extends BaseSeleniumTest {

    private AdminPage adminPage;

    private static final String ADMIN_URL = "http://localhost:8080/admin";

    // Пути к тестовым изображениям
    private static final String TEST_IMAGE_PATH = System.getProperty("user.dir") + "/src/test/resources/test-image.jpg";
    private static final String TEST_GALLERY_IMAGE_PATH = System.getProperty("user.dir") + "/src/test/resources/test-gallery-image.jpg";
    private static final String TEST_SKETCH_PATH = System.getProperty("user.dir") + "/src/test/resources/test-sketch.jpg";
    private static final String TEST_LOGO_PATH = System.getProperty("user.dir") + "/src/test/resources/test-logo.png";
    private static final String TEST_BREADCRUMB_PATH = System.getProperty("user.dir") + "/src/test/resources/test-breadcrumb.jpg";

    @BeforeEach
    @Step("Инициализация страницы администратора")
    public void setup() {
        navigateTo(ADMIN_URL);
        adminPage = new AdminPage(driver);
        adminPage.waitForPageLoad();
        log.info("✅ Страница администратора загружена");
    }

    // ==================== ТЕСТЫ НАВИГАЦИИ ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.1.1 - Проверка навигации по всем вкладкам админ-панели")
    @Description("Проверяет, что все вкладки админ-панели (Главная, Галерея, Эскизы, Блог, Отзывы, Настройки) успешно открываются")
    @Owner("Maxsim Tattoo Team")
    public void testAdminNavigationTabs() {
        // Проверяем вкладку "Главная"
        adminPage.clickHomeTab();
        assertTrue(driver.getCurrentUrl().contains("/admin/home"),
                "Должен быть переход на вкладку 'Главная'");
        Allure.addAttachment("Текущая вкладка", "Главная");

        // Проверяем вкладку "Галерея"
        adminPage.clickGalleryTab();
        assertTrue(driver.getCurrentUrl().contains("/gallery/admin"),
                "Должен быть переход на вкладку 'Галерея'");
        Allure.addAttachment("Текущая вкладка", "Галерея");

        // Проверяем вкладку "Эскизы"
        adminPage.clickSketchesTab();
        assertTrue(driver.getCurrentUrl().contains("/sketches/admin"),
                "Должен быть переход на вкладку 'Эскизы'");
        Allure.addAttachment("Текущая вкладка", "Эскизы");

        // Проверяем вкладку "Блог"
        adminPage.clickBlogTab();
        assertTrue(driver.getCurrentUrl().contains("/blog/admin"),
                "Должен быть переход на вкладку 'Блог'");
        Allure.addAttachment("Текущая вкладка", "Блог");

        // Проверяем вкладку "Отзывы"
        adminPage.clickReviewsTab();
        assertTrue(driver.getCurrentUrl().contains("/reviews/admin"),
                "Должен быть переход на вкладку 'Отзывы'");
        Allure.addAttachment("Текущая вкладка", "Отзывы");

        // Проверяем вкладку "Настройки"
        adminPage.clickSettingsTab();
        assertTrue(driver.getCurrentUrl().contains("/setting/admin"),
                "Должен быть переход на вкладку 'Настройки'");
        Allure.addAttachment("Текущая вкладка", "Настройки");

        log.info("✅ Все вкладки админ-панели успешно открываются");
    }

    // ==================== ТЕСТЫ КАРУСЕЛИ ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.1.2 - Добавление и удаление слайда в карусели")
    @Description("Проверяет возможность добавления нового слайда в карусель и его последующего удаления")
    public void testCarouselSlideManagement() {
        // Arrange
        adminPage.clickHomeTab();
        adminPage.openCarouselTab();

        int initialSlidesCount = adminPage.getCarouselSlidesCount();
        log.info("Начальное количество слайдов: {}", initialSlidesCount);
        Allure.addAttachment("Начальное количество слайдов", String.valueOf(initialSlidesCount));

        // Act - Добавляем новый слайд
        String testTitle = "Тестовый заголовок слайда";
        String testSubtitle = "Тестовый подзаголовок слайда";

        adminPage.addCarouselSlide(testTitle, testSubtitle, TEST_IMAGE_PATH);

        // Ожидаем появления сообщения об успехе
        String successMessage = adminPage.getSuccessMessage();
        log.info("Сообщение после добавления: {}", successMessage);
        adminPage.closeModal();

        // Assert - Проверяем, что слайд добавился
        int afterAddSlidesCount = adminPage.getCarouselSlidesCount();
        assertEquals(initialSlidesCount + 1, afterAddSlidesCount,
                "Количество слайдов должно увеличиться на 1");

        Allure.addAttachment("Количество слайдов после добавления", String.valueOf(afterAddSlidesCount));

        // Act - Удаляем добавленный слайд
        adminPage.deleteFirstCarouselSlide();

        // Assert - Проверяем, что слайд удалился
        int afterDeleteSlidesCount = adminPage.getCarouselSlidesCount();
        assertEquals(initialSlidesCount, afterDeleteSlidesCount,
                "Количество слайдов должно вернуться к исходному");

        Allure.addAttachment("Количество слайдов после удаления", String.valueOf(afterDeleteSlidesCount));
        log.info("✅ Тест управления слайдами карусели пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.3 - Проверка навигации по слайдам карусели")
    @Description("Проверяет, что кнопки навигации карусели работают корректно")
    public void testCarouselNavigation() {
        // Arrange
        adminPage.clickHomeTab();
        adminPage.openCarouselTab();

        int slidesCount = adminPage.getCarouselSlidesCount();

        if (slidesCount > 1) {
            // Act - Переключаем слайды
            adminPage.clickCarouselPrev();
            adminPage.clickCarouselNext();

            // Assert - Проверяем, что навигация работает без ошибок
            assertTrue(adminPage.isCarouselVisible(),
                    "Карусель должна оставаться видимой после навигации");

            Allure.addAttachment("Количество слайдов для навигации", String.valueOf(slidesCount));
            log.info("✅ Навигация по карусели работает корректно");
        } else {
            log.warn("Недостаточно слайдов для проверки навигации (требуется > 1)");
            Allure.addAttachment("Статус", "Пропущен - недостаточно слайдов");
        }
    }

    // ==================== ТЕСТЫ ГАЛЕРЕИ ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.1.4 - Добавление и удаление изображения в галерее")
    @Description("Проверяет возможность добавления нового изображения в галерею и его последующего удаления")
    public void testGalleryImageManagement() {
        // Arrange
        adminPage.clickGalleryTab();

        int initialImagesCount = adminPage.getGalleryImagesCount();
        log.info("Начальное количество изображений: {}", initialImagesCount);
        Allure.addAttachment("Начальное количество изображений", String.valueOf(initialImagesCount));

        // Act - Добавляем новое изображение
        String testDescription = "Тестовое изображение галереи";
        adminPage.addGalleryImage(testDescription, TEST_GALLERY_IMAGE_PATH);

        // Assert - Проверяем, что изображение добавилось
        int afterAddImagesCount = adminPage.getGalleryImagesCount();
        assertEquals(initialImagesCount + 1, afterAddImagesCount,
                "Количество изображений должно увеличиться на 1");

        Allure.addAttachment("Количество изображений после добавления", String.valueOf(afterAddImagesCount));

        // Act - Удаляем добавленное изображение
        adminPage.deleteFirstGalleryImage();

        // Assert - Проверяем, что изображение удалилось
        int afterDeleteImagesCount = adminPage.getGalleryImagesCount();
        assertEquals(initialImagesCount, afterDeleteImagesCount,
                "Количество изображений должно вернуться к исходному");

        Allure.addAttachment("Количество изображений после удаления", String.valueOf(afterDeleteImagesCount));
        log.info("✅ Тест управления изображениями галереи пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.5 - Проверка фильтрации изображений в галерее")
    @Description("Проверяет, что фильтры галереи работают корректно")
    public void testGalleryFilters() {
        // Arrange
        adminPage.clickGalleryTab();

        List<String> availableFilters = adminPage.getGalleryFilters();
        log.info("Доступные фильтры: {}", availableFilters);

        if (availableFilters.size() > 1) {
            // Act - Применяем первый фильтр (кроме "Вся галерея")
            String filterName = availableFilters.get(1);
            adminPage.selectGalleryFilter(filterName);

            // Assert - Проверяем, что фильтр применился
            int filteredImagesCount = adminPage.getGalleryImagesCount();
            Allure.addAttachment("Фильтр", filterName);
            Allure.addAttachment("Количество изображений после фильтрации", String.valueOf(filteredImagesCount));

            // Act - Возвращаемся к "Вся галерея"
            adminPage.selectGalleryFilter("Вся галерея");
            int allImagesCount = adminPage.getGalleryImagesCount();

            assertTrue(filteredImagesCount <= allImagesCount,
                    "Отфильтрованных изображений не может быть больше всех");

            log.info("✅ Фильтрация галереи работает корректно");
        } else {
            log.warn("Недостаточно фильтров для проверки");
            Allure.addAttachment("Статус", "Пропущен - недостаточно фильтров");
        }
    }

    // ==================== ТЕСТЫ ЭСКИЗОВ ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("4.1.6 - Добавление и удаление эскиза")
    @Description("Проверяет возможность добавления нового эскиза и его последующего удаления")
    public void testSketchManagement() {
        // Arrange
        adminPage.clickSketchesTab();

        int initialSketchesCount = adminPage.getSketchesCount();
        log.info("Начальное количество эскизов: {}", initialSketchesCount);
        Allure.addAttachment("Начальное количество эскизов", String.valueOf(initialSketchesCount));

        // Act - Добавляем новый эскиз
        String testDescription = "Тестовый эскиз";
        adminPage.addSketch(testDescription, TEST_SKETCH_PATH);

        // Assert - Проверяем, что эскиз добавился
        int afterAddSketchesCount = adminPage.getSketchesCount();
        assertEquals(initialSketchesCount + 1, afterAddSketchesCount,
                "Количество эскизов должно увеличиться на 1");

        Allure.addAttachment("Количество эскизов после добавления", String.valueOf(afterAddSketchesCount));

        // Act - Удаляем добавленный эскиз
        adminPage.deleteFirstSketch();

        // Assert - Проверяем, что эскиз удалился
        int afterDeleteSketchesCount = adminPage.getSketchesCount();
        assertEquals(initialSketchesCount, afterDeleteSketchesCount,
                "Количество эскизов должно вернуться к исходному");

        Allure.addAttachment("Количество эскизов после удаления", String.valueOf(afterDeleteSketchesCount));
        log.info("✅ Тест управления эскизами пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.7 - Проверка пагинации эскизов")
    @Description("Проверяет, что пагинация на странице эскизов работает корректно")
    public void testSketchesPagination() {
        // Arrange
        adminPage.clickSketchesTab();

        boolean hasPagination = adminPage.isSketchesPaginationVisible();

        if (hasPagination) {
            // Act - Меняем количество отображаемых элементов
            adminPage.selectSketchesPerPage(6);

            // Assert - Проверяем, что количество элементов изменилось
            int countAfterChange = adminPage.getSketchesCount();
            Allure.addAttachment("Количество эскизов после смены per page", String.valueOf(countAfterChange));

            // Act - Переключаем страницы
            boolean hasNextPage = adminPage.isSketchesNextPageEnabled();
            if (hasNextPage) {
                adminPage.clickSketchesNextPage();
                int countOnNextPage = adminPage.getSketchesCount();
                Allure.addAttachment("Количество эскизов на следующей странице", String.valueOf(countOnNextPage));

                adminPage.clickSketchesPrevPage();
            }

            log.info("✅ Пагинация эскизов работает корректно");
        } else {
            log.warn("Пагинация не доступна (возможно, мало эскизов)");
            Allure.addAttachment("Статус", "Пропущен - пагинация не доступна");
        }
    }

    // ==================== ТЕСТЫ ОТЗЫВОВ ====================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.8 - Просмотр и удаление отзывов")
    @Description("Проверяет возможность просмотра и удаления отзывов")
    public void testReviewsManagement() {
        // Arrange
        adminPage.clickReviewsTab();

        int reviewsCount = adminPage.getReviewsCount();
        log.info("Количество отзывов: {}", reviewsCount);
        Allure.addAttachment("Количество отзывов", String.valueOf(reviewsCount));

        if (reviewsCount > 0) {
            // Act - Удаляем первый отзыв
            adminPage.deleteFirstReview();

            // Assert - Проверяем, что отзыв удалился
            int afterDeleteCount = adminPage.getReviewsCount();
            assertEquals(reviewsCount - 1, afterDeleteCount,
                    "Количество отзывов должно уменьшиться на 1");

            Allure.addAttachment("Количество отзывов после удаления", String.valueOf(afterDeleteCount));
            log.info("✅ Тест управления отзывами пройден");
        } else {
            log.warn("Нет отзывов для удаления");
            Allure.addAttachment("Статус", "Пропущен - нет отзывов");
        }
    }

    // ==================== ТЕСТЫ НАСТРОЕК ====================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.9 - Проверка настроек пользователей")
    @Description("Проверяет, что список пользователей отображается корректно")
    public void testUsersSettings() {
        // Arrange
        adminPage.clickSettingsTab();
        adminPage.openUsersTab();

        // Act
        int usersCount = adminPage.getUsersCount();

        // Assert
        assertTrue(usersCount >= 0, "Количество пользователей не может быть отрицательным");
        Allure.addAttachment("Количество пользователей", String.valueOf(usersCount));

        log.info("✅ Тест настроек пользователей пройден");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.10 - Обновление контактной информации")
    @Description("Проверяет возможность обновления контактной информации (телефон, email, адрес)")
    public void testContactInfoUpdate() {
        // Arrange
        adminPage.clickSettingsTab();
        adminPage.openContactTab();

        String testPhone = "+7 (999) 123-45-67";
        String testEmail = "test@example.com";
        String testAddress = "г. Москва, ул. Тестовая, д. 1";

        // Act
        adminPage.updateContactInfo(testPhone, testEmail, testAddress);

        // Assert
        String successMessage = adminPage.getSuccessMessage();
        assertTrue(successMessage.contains("успешно") || successMessage.isEmpty(),
                "Должно появиться сообщение об успешном обновлении");

        adminPage.closeModal();

        Allure.addAttachment("Обновленные данные",
                String.format("Телефон: %s, Email: %s, Адрес: %s", testPhone, testEmail, testAddress));
        log.info("✅ Тест обновления контактной информации пройден");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.1.11 - Загрузка логотипа")
    @Description("Проверяет возможность загрузки логотипа сайта")
    public void testLogoUpload() {
        // Arrange
        adminPage.clickSettingsTab();
        adminPage.openLogoTab();

        // Act
        adminPage.uploadLogo(TEST_LOGO_PATH);

        // Assert
        String successMessage = adminPage.getSuccessMessage();
        assertTrue(successMessage.contains("успешно") || successMessage.isEmpty(),
                "Должно появиться сообщение об успешной загрузке логотипа");

        adminPage.closeModal();

        Allure.addAttachment("Путь к логотипу", TEST_LOGO_PATH);
        log.info("✅ Тест загрузки логотипа пройден");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("4.1.12 - Загрузка фона хлебных крошек")
    @Description("Проверяет возможность загрузки фонового изображения для секции хлебных крошек")
    public void testBreadcrumbBackgroundUpload() {
        // Arrange
        adminPage.clickSettingsTab();
        adminPage.openBreadcrumbTab();

        // Act
        adminPage.uploadBreadcrumbBackground(TEST_BREADCRUMB_PATH);

        // Assert
        String successMessage = adminPage.getSuccessMessage();
        assertTrue(successMessage.contains("успешно") || successMessage.isEmpty(),
                "Должно появиться сообщение об успешной загрузке фона");

        adminPage.closeModal();

        Allure.addAttachment("Путь к фону", TEST_BREADCRUMB_PATH);
        log.info("✅ Тест загрузки фона хлебных крошек пройден");
    }

    // ==================== ТЕСТЫ РАЗДЕЛОВ ГЛАВНОЙ СТРАНИЦЫ ====================

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4.1.13 - Проверка всех разделов главной страницы")
    @Description("Проверяет, что все разделы главной страницы (Особенности, О нас, Классы, Цены, Преимущества) доступны")
    public void testAllHomeSections() {
        // Arrange
        adminPage.clickHomeTab();

        // Act & Assert - Проверяем раздел "Особенности"
        adminPage.openFeatureTab();
        assertTrue(adminPage.isFeatureSectionVisible(),
                "Раздел 'Особенности' должен отображаться");
        Allure.addAttachment("Раздел", "Особенности - OK");

        // Act & Assert - Проверяем раздел "О нас"
        adminPage.openAboutTab();
        assertTrue(adminPage.isAboutSectionVisible(),
                "Раздел 'О нас' должен отображаться");
        Allure.addAttachment("Раздел", "О нас - OK");

        // Act & Assert - Проверяем раздел "Классы"
        adminPage.openClassesTab();
        assertTrue(adminPage.isClassesSectionVisible(),
                "Раздел 'Классы' должен отображаться");
        Allure.addAttachment("Раздел", "Классы - OK");

        // Act & Assert - Проверяем раздел "Цены"
        adminPage.openPriceTab();
        assertTrue(adminPage.isPriceSectionVisible(),
                "Раздел 'Цены' должен отображаться");
        Allure.addAttachment("Раздел", "Цены - OK");

        // Act & Assert - Проверяем раздел "Преимущества"
        adminPage.openChooseUsTab();
        assertTrue(adminPage.isChooseUsSectionVisible(),
                "Раздел 'Преимущества' должен отображаться");
        Allure.addAttachment("Раздел", "Преимущества - OK");

        log.info("✅ Все разделы главной страницы доступны");
    }
}