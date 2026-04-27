package ru.tattoo.maxsim.UI.page;

import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AdminPage {

    private static final Logger log = LoggerFactory.getLogger(AdminPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ==================== КОНСТАНТЫ ====================
    private static final String ADMIN_URL = "http://localhost:8080/admin";
    private static final String LOGIN_URL = "http://localhost:8080/login";
    private static final String DEFAULT_ADMIN_LOGIN = "ADMIN";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";

    // ==================== ЭЛЕМЕНТЫ АВТОРИЗАЦИИ ====================
    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(css = ".map-contact-form")
    private WebElement loginForm;

    @FindBy(css = ".form-signin")
    private WebElement formSignin;

    @FindBy(css = ".breadcrumb-section .form-input")
    private WebElement loginContainer;

    @FindBy(css = "a[href='/registration']")
    private WebElement registrationLink;

    @FindBy(css = ".alert-danger, .error-message, .text-danger")
    private WebElement loginErrorMessage;

    // ==================== ВКЛАДКИ НАВИГАЦИИ ====================
    @FindBy(css = "a[href='/admin/home']")
    private WebElement homeTab;

    @FindBy(css = "a[href='/gallery/admin']")
    private WebElement galleryTab;

    @FindBy(css = "a[href='/sketches/admin']")
    private WebElement sketchesTab;

    @FindBy(css = "a[href='/blog/admin']")
    private WebElement blogTab;

    @FindBy(css = "a[href='/reviews/admin']")
    private WebElement reviewsTab;

    @FindBy(css = "a[href='/setting/admin']")
    private WebElement settingsTab;

    // ==================== РАЗДЕЛ "ГЛАВНАЯ" - ЛЕВАЯ НАВИГАЦИЯ ====================
    @FindBy(id = "v-pills-carousel-tab")
    private WebElement carouselTab;

    @FindBy(id = "v-pills-feature-tab")
    private WebElement featureTab;

    @FindBy(id = "v-pills-about-tab")
    private WebElement aboutTab;

    @FindBy(id = "v-pills-classes-tab")
    private WebElement classesTab;

    @FindBy(id = "v-pills-price-tab")
    private WebElement priceTab;

    @FindBy(id = "v-pills-chooseus-tab")
    private WebElement chooseUsTab;

    // ==================== ФОРМЫ ====================
    @FindBy(css = ".modern-form")
    private WebElement carouselForm;

    @FindBy(name = "title")
    private WebElement carouselTitleInput;

    @FindBy(name = "subtitle")
    private WebElement carouselSubtitleInput;

    @FindBy(css = ".btn-modern")
    private WebElement saveSlideButton;

    // ==================== ЭЛЕМЕНТЫ КАРУСЕЛИ ====================
    @FindBy(css = "#adminCarousel .carousel-item")
    private List<WebElement> carouselSlides;

    @FindBy(css = ".admin-delete-btn")
    private List<WebElement> deleteSlideButtons;

    @FindBy(css = ".carousel-control-prev")
    private WebElement carouselPrevButton;

    @FindBy(css = ".carousel-control-next")
    private WebElement carouselNextButton;

    @FindBy(css = "#adminCarousel")
    private WebElement carouselContainer;

    // ==================== ЭЛЕМЕНТЫ ГАЛЕРЕИ ====================
    @FindBy(css = ".img-import .reviews-admin")
    private List<WebElement> galleryImages;

    @FindBy(css = ".delete-button")
    private List<WebElement> deleteImageButtons;

    @FindBy(name = "description")
    private WebElement galleryDescriptionInput;

    @FindBy(id = "file-img")
    private WebElement galleryFileInput;

    @FindBy(css = ".gallery-controls ul li")
    private List<WebElement> galleryFilters;

    @FindBy(css = ".btn-primary")
    private List<WebElement> primaryButtons;

    // ==================== ЭЛЕМЕНТЫ ЭСКИЗОВ ====================
    @FindBy(css = ".sketches-import .reviews-admin")
    private List<WebElement> sketchesItems;

    @FindBy(id = "sketchers-number")
    private WebElement sketchesPerPageSelect;

    @FindBy(id = "sketchers-left")
    private WebElement sketchesPrevPageButton;

    @FindBy(id = "sketchers-right")
    private WebElement sketchesNextPageButton;

    @FindBy(id = "file-sketches")
    private WebElement sketchesFileInput;

    // ==================== ЭЛЕМЕНТЫ ОТЗЫВОВ ====================
    @FindBy(css = ".reviews .card")
    private List<WebElement> reviewCards;

    @FindBy(css = ".del-reviews")
    private List<WebElement> deleteReviewButtons;

    // ==================== ЭЛЕМЕНТЫ НАСТРОЕК ====================
    @FindBy(id = "v-pills-users-tab")
    private WebElement usersTab;

    @FindBy(id = "v-pills-contact-tab")
    private WebElement contactTab;

    @FindBy(id = "v-breadcrumb-section-tab")
    private WebElement breadcrumbTab;

    @FindBy(id = "v-logo-tab")
    private WebElement logoTab;

    @FindBy(css = ".user .card")
    private List<WebElement> userCards;

    @FindBy(id = "tell")
    private WebElement phoneInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "address")
    private WebElement addressInput;

    @FindBy(id = "file-input-logo")
    private WebElement logoFileInput;

    @FindBy(id = "file-input-breadcrumb")
    private WebElement breadcrumbFileInput;

    // ==================== МОДАЛЬНОЕ ОКНО ====================
    @FindBy(id = "myModal")
    private WebElement modal;

    @FindBy(css = ".modal-body .text-success")
    private WebElement successMessage;

    @FindBy(css = ".modal-body .text-danger")
    private WebElement errorMessage;

    @FindBy(css = ".modal-footer .btn-secondary")
    private WebElement closeModalButton;

    // ==================== КОНСТРУКТОР ====================
    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // ==================== МЕТОДЫ АВТОРИЗАЦИИ ====================

    @Step("Выполнение входа в админ-панель с логином: {login}")
    public void login(String login, String password) {
        log.info("🔐 Выполнение входа в админ-панель: login={}", login);

        if (isOnLoginPage()) {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

                WebElement usernameField = driver.findElement(By.name("username"));
                WebElement passwordField = driver.findElement(By.name("password"));
                WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

                usernameField.clear();
                usernameField.sendKeys(login);

                passwordField.clear();
                passwordField.sendKeys(password);

                log.info("📝 Введены учетные данные: username={}", login);

                submitButton.click();
                log.info("✅ Форма авторизации отправлена");

                waitForLoginComplete();

            } catch (Exception e) {
                log.error("❌ Ошибка при авторизации: {}", e.getMessage());
                throw new RuntimeException("Ошибка авторизации", e);
            }
        } else {
            log.info("✅ Уже авторизованы, вход не требуется");
        }
    }

    @Step("Выполнение входа с учетными данными по умолчанию")
    public void loginWithDefaultCredentials() {
        login(DEFAULT_ADMIN_LOGIN, DEFAULT_ADMIN_PASSWORD);
    }

    @Step("Проверка нахождения на странице логина")
    public boolean isOnLoginPage() {
        try {
            String currentUrl = driver.getCurrentUrl();
            boolean isLoginUrl = currentUrl.contains("/login");

            List<WebElement> usernameFields = driver.findElements(By.name("username"));
            List<WebElement> passwordFields = driver.findElements(By.name("password"));

            boolean hasUsernameField = !usernameFields.isEmpty();
            boolean hasPasswordField = !passwordFields.isEmpty();

            return isLoginUrl || (hasUsernameField && hasPasswordField);

        } catch (Exception e) {
            log.debug("Ошибка при проверке страницы логина: {}", e.getMessage());
            return false;
        }
    }

    @Step("Ожидание завершения процесса авторизации")
    public void waitForLoginComplete() {
        try {
            wait.until(driver -> {
                String currentUrl = driver.getCurrentUrl();

                if (currentUrl.contains("/admin")) {
                    return true;
                }

                if (currentUrl.contains("/login?error")) {
                    throw new RuntimeException("Ошибка авторизации: неверные учетные данные");
                }

                return false;
            });

            Thread.sleep(1000);
            log.info("✅ Авторизация успешно завершена");

        } catch (Exception e) {
            log.warn("Авторизация не завершилась: {}", e.getMessage());

            if (isLoginErrorDisplayed()) {
                String errorText = getLoginErrorMessage();
                log.error("❌ Ошибка авторизации: {}", errorText);
                throw new RuntimeException("Ошибка авторизации: " + errorText);
            }

            throw new RuntimeException("Авторизация не завершилась за отведенное время", e);
        }
    }

    @Step("Проверка успешной авторизации")
    public boolean isLoggedIn() {
        try {
            boolean hasHomeTab = homeTab != null && homeTab.isDisplayed();
            boolean hasGalleryTab = galleryTab != null && galleryTab.isDisplayed();
            boolean hasAdminUrl = driver.getCurrentUrl().contains("/admin");

            return hasHomeTab || hasGalleryTab || hasAdminUrl;

        } catch (Exception e) {
            log.debug("Проверка авторизации не пройдена: {}", e.getMessage());
            return false;
        }
    }

    @Step("Проверка отображения ошибки авторизации")
    public boolean isLoginErrorDisplayed() {
        try {
            List<WebElement> errors = driver.findElements(By.cssSelector(".alert-danger, .error-message, .text-danger"));
            return !errors.isEmpty() && errors.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение текста ошибки авторизации")
    public String getLoginErrorMessage() {
        try {
            List<WebElement> errors = driver.findElements(By.cssSelector(".alert-danger, .error-message, .text-danger"));
            if (!errors.isEmpty()) {
                return errors.get(0).getText();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    @Step("Выход из админ-панели")
    public void logout() {
        try {
            By logoutSelector = By.cssSelector(".logout-btn, a[href='/logout'], .sign-out");
            WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(logoutSelector));
            logoutButton.click();
            log.info("✅ Выполнен выход из админ-панели");
            wait.until(ExpectedConditions.urlContains("/login"));
        } catch (Exception e) {
            log.warn("Кнопка выхода не найдена: {}", e.getMessage());
            driver.get(LOGIN_URL);
            log.info("🔄 Выполнен принудительный переход на страницу логина");
        }
    }

    @Step("Переход на страницу регистрации")
    public void goToRegistrationPage() {
        wait.until(ExpectedConditions.elementToBeClickable(registrationLink)).click();
        log.info("✅ Переход на страницу регистрации");
    }

    @Step("Проверка доступности ссылки на регистрацию")
    public boolean isRegistrationLinkVisible() {
        try {
            return registrationLink != null && registrationLink.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== МЕТОДЫ ОЖИДАНИЯ ====================

    @Step("Ожидание загрузки страницы админ-панели")
    public void waitForPageLoad() {
        try {
            if (isOnLoginPage()) {
                loginWithDefaultCredentials();
            }

            wait.until(ExpectedConditions.visibilityOf(homeTab));
            log.info("✅ Страница админ-панели загружена");
        } catch (Exception e) {
            log.error("Не удалось загрузить админ-панель: {}", e.getMessage());

            if (isOnLoginPage()) {
                log.info("🔄 Повторная попытка авторизации...");
                loginWithDefaultCredentials();
                wait.until(ExpectedConditions.visibilityOf(homeTab));
            } else {
                throw new RuntimeException("Ошибка загрузки админ-панели", e);
            }
        }
    }

    @Step("Ожидание видимости модального окна")
    public void waitForModalVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(modal));
        } catch (Exception e) {
            log.debug("Модальное окно не появилось: {}", e.getMessage());
        }
    }

    // ==================== МЕТОДЫ НАВИГАЦИИ ====================

    @Step("Переход на вкладку 'Главная'")
    public void clickHomeTab() {
        wait.until(ExpectedConditions.elementToBeClickable(homeTab)).click();
        log.info("✅ Переход на вкладку 'Главная'");
    }

    @Step("Переход на вкладку 'Галерея'")
    public void clickGalleryTab() {
        wait.until(ExpectedConditions.elementToBeClickable(galleryTab)).click();
        log.info("✅ Переход на вкладку 'Галерея'");
    }

    @Step("Переход на вкладку 'Эскизы'")
    public void clickSketchesTab() {
        wait.until(ExpectedConditions.elementToBeClickable(sketchesTab)).click();
        log.info("✅ Переход на вкладку 'Эскизы'");
    }

    @Step("Переход на вкладку 'Блог'")
    public void clickBlogTab() {
        wait.until(ExpectedConditions.elementToBeClickable(blogTab)).click();
        log.info("✅ Переход на вкладку 'Блог'");
    }

    @Step("Переход на вкладку 'Отзывы'")
    public void clickReviewsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(reviewsTab)).click();
        log.info("✅ Переход на вкладку 'Отзывы'");
    }

    @Step("Переход на вкладку 'Настройки'")
    public void clickSettingsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(settingsTab)).click();
        log.info("✅ Переход на вкладку 'Настройки'");
    }

    // ==================== МЕТОДЫ РАЗДЕЛА "ГЛАВНАЯ" ====================

    @Step("Открытие вкладки 'Карусель'")
    public void openCarouselTab() {
        wait.until(ExpectedConditions.elementToBeClickable(carouselTab)).click();
        log.info("✅ Открыта вкладка 'Карусель'");
    }

    @Step("Открытие вкладки 'Особенности'")
    public void openFeatureTab() {
        wait.until(ExpectedConditions.elementToBeClickable(featureTab)).click();
        log.info("✅ Открыта вкладка 'Особенности'");
    }

    @Step("Открытие вкладки 'О нас'")
    public void openAboutTab() {
        wait.until(ExpectedConditions.elementToBeClickable(aboutTab)).click();
        log.info("✅ Открыта вкладка 'О нас'");
    }

    @Step("Открытие вкладки 'Классы'")
    public void openClassesTab() {
        wait.until(ExpectedConditions.elementToBeClickable(classesTab)).click();
        log.info("✅ Открыта вкладка 'Классы'");
    }

    @Step("Открытие вкладки 'Цены'")
    public void openPriceTab() {
        wait.until(ExpectedConditions.elementToBeClickable(priceTab)).click();
        log.info("✅ Открыта вкладка 'Цены'");
    }

    @Step("Открытие вкладки 'Преимущества'")
    public void openChooseUsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(chooseUsTab)).click();
        log.info("✅ Открыта вкладка 'Преимущества'");
    }

    // ==================== МЕТОДЫ КАРУСЕЛИ ====================

    @Step("Добавление слайда в карусель")
    public void addCarouselSlide(String title, String subtitle, String imagePath) {
        wait.until(ExpectedConditions.visibilityOf(carouselForm));

        carouselTitleInput.clear();
        carouselTitleInput.sendKeys(title);

        carouselSubtitleInput.clear();
        carouselSubtitleInput.sendKeys(subtitle);

        uploadFile(driver.findElement(By.id("file-carousel")), imagePath);

        wait.until(ExpectedConditions.elementToBeClickable(saveSlideButton)).click();
        log.info("✅ Слайд добавлен: title={}, subtitle={}", title, subtitle);
    }

    @Step("Редактирование слайда карусели")
    public void editCarouselSlide(String slideId, String newTitle, String newSubtitle) {
        try {
            WebElement editForm = driver.findElement(By.id("edit-form-" + slideId));
            WebElement titleInput = editForm.findElement(By.name("title"));
            WebElement subtitleInput = editForm.findElement(By.name("subtitle"));
            WebElement saveButton = editForm.findElement(By.cssSelector(".btn-modern, .btn-primary"));

            titleInput.clear();
            titleInput.sendKeys(newTitle);

            subtitleInput.clear();
            subtitleInput.sendKeys(newSubtitle);

            saveButton.click();
            log.info("✅ Слайд отредактирован: id={}, title={}", slideId, newTitle);
        } catch (Exception e) {
            log.error("Ошибка при редактировании слайда: {}", e.getMessage());
            throw e;
        }
    }

    @Step("Получение количества слайдов в карусели")
    public int getCarouselSlidesCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(carouselSlides));
            return carouselSlides.size();
        } catch (Exception e) {
            log.warn("Не удалось получить слайды карусели: {}", e.getMessage());
            return 0;
        }
    }

    @Step("Удаление первого слайда карусели")
    public void deleteFirstCarouselSlide() {
        wait.until(ExpectedConditions.visibilityOfAllElements(deleteSlideButtons));
        if (!deleteSlideButtons.isEmpty()) {
            deleteSlideButtons.get(0).click();
            waitForModalVisible();
            closeModal();
            log.info("✅ Первый слайд удален");
        }
    }

    @Step("Удаление слайда по ID")
    public void deleteCarouselSlideById(String slideId) {
        try {
            WebElement deleteButton = driver.findElement(By.id("delete-" + slideId));
            deleteButton.click();
            waitForModalVisible();
            closeModal();
            log.info("✅ Слайд с ID {} удален", slideId);
        } catch (Exception e) {
            log.warn("Не удалось удалить слайд: {}", e.getMessage());
        }
    }

    @Step("Получение ID последнего слайда карусели")
    public String getLastCarouselSlideId() {
        try {
            if (!deleteSlideButtons.isEmpty()) {
                return deleteSlideButtons.get(0).getAttribute("id");
            }
            return null;
        } catch (Exception e) {
            log.warn("Не удалось получить ID слайда: {}", e.getMessage());
            return null;
        }
    }

    @Step("Проверка наличия заголовка слайда")
    public boolean isCarouselSlideTitlePresent(String title) {
        try {
            for (WebElement slide : carouselSlides) {
                if (slide.getText().contains(title)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Нажатие кнопки 'Предыдущий слайд'")
    public void clickCarouselPrev() {
        wait.until(ExpectedConditions.elementToBeClickable(carouselPrevButton)).click();
        log.info("⬅️ Нажата кнопка предыдущего слайда");
    }

    @Step("Нажатие кнопки 'Следующий слайд'")
    public void clickCarouselNext() {
        wait.until(ExpectedConditions.elementToBeClickable(carouselNextButton)).click();
        log.info("➡️ Нажата кнопка следующего слайда");
    }

    @Step("Проверка видимости карусели")
    public boolean isCarouselVisible() {
        try {
            return carouselContainer.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== МЕТОДЫ ГАЛЕРЕИ ====================

    @Step("Добавление изображения в галерею")
    public void addGalleryImage(String description, String imagePath) {
        wait.until(ExpectedConditions.visibilityOf(galleryDescriptionInput));

        galleryDescriptionInput.clear();
        galleryDescriptionInput.sendKeys(description);

        uploadFile(galleryFileInput, imagePath);

        clickPrimaryButton();
        log.info("✅ Изображение добавлено в галерею: description={}", description);
    }

    @Step("Редактирование описания изображения галереи")
    public void editGalleryImageDescription(String imageId, String newDescription) {
        try {
            WebElement editForm = driver.findElement(By.id("edit-form-" + imageId));
            WebElement descriptionInput = editForm.findElement(By.name("description"));
            WebElement saveButton = editForm.findElement(By.cssSelector(".btn-primary"));

            descriptionInput.clear();
            descriptionInput.sendKeys(newDescription);

            saveButton.click();
            log.info("✅ Описание изображения отредактировано: id={}", imageId);
        } catch (Exception e) {
            log.error("Ошибка при редактировании описания: {}", e.getMessage());
            throw e;
        }
    }

    @Step("Получение количества изображений в галерее")
    public int getGalleryImagesCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(galleryImages));
            return galleryImages.size();
        } catch (Exception e) {
            log.warn("Не удалось получить изображения галереи: {}", e.getMessage());
            return 0;
        }
    }

    @Step("Удаление первого изображения галереи")
    public void deleteFirstGalleryImage() {
        wait.until(ExpectedConditions.visibilityOfAllElements(deleteImageButtons));
        if (!deleteImageButtons.isEmpty()) {
            deleteImageButtons.get(0).click();
            waitForModalVisible();
            closeModal();
            log.info("✅ Первое изображение галереи удалено");
        }
    }

    @Step("Удаление изображения галереи по ID")
    public void deleteGalleryImageById(String imageId) {
        try {
            WebElement deleteButton = driver.findElement(By.id("delete-" + imageId));
            deleteButton.click();
            waitForModalVisible();
            closeModal();
            log.info("✅ Изображение с ID {} удалено", imageId);
        } catch (Exception e) {
            log.warn("Не удалось удалить изображение: {}", e.getMessage());
        }
    }

    @Step("Получение ID последнего изображения галереи")
    public String getLastGalleryImageId() {
        try {
            if (!deleteImageButtons.isEmpty()) {
                return deleteImageButtons.get(0).getAttribute("id");
            }
            return null;
        } catch (Exception e) {
            log.warn("Не удалось получить ID изображения: {}", e.getMessage());
            return null;
        }
    }

    @Step("Проверка наличия описания изображения")
    public boolean isGalleryImageDescriptionPresent(String description) {
        try {
            List<WebElement> descriptions = driver.findElements(By.cssSelector(".gallery-item .description, .reviews-admin .carousel-caption h4"));
            for (WebElement desc : descriptions) {
                if (desc.getText().contains(description)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получение списка фильтров галереи")
    public List<String> getGalleryFilters() {
        wait.until(ExpectedConditions.visibilityOfAllElements(galleryFilters));
        return galleryFilters.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    @Step("Выбор фильтра галереи: {filterName}")
    public void selectGalleryFilter(String filterName) {
        for (WebElement filter : galleryFilters) {
            if (filter.getText().equals(filterName)) {
                filter.click();
                log.info("✅ Выбран фильтр: {}", filterName);
                break;
            }
        }
    }

    // ==================== МЕТОДЫ ЭСКИЗОВ ====================

    @Step("Добавление эскиза")
    public void addSketch(String description, String imagePath) {
        WebElement descriptionInput = driver.findElement(By.name("description"));
        descriptionInput.clear();
        descriptionInput.sendKeys(description);

        uploadFile(sketchesFileInput, imagePath);

        clickPrimaryButton();
        log.info("✅ Эскиз добавлен: description={}", description);
    }

    @Step("Получение количества эскизов")
    public int getSketchesCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(sketchesItems));
            return sketchesItems.size();
        } catch (Exception e) {
            log.warn("Не удалось получить эскизы: {}", e.getMessage());
            return 0;
        }
    }

    @Step("Удаление первого эскиза")
    public void deleteFirstSketch() {
        wait.until(ExpectedConditions.visibilityOfAllElements(deleteImageButtons));
        if (!deleteImageButtons.isEmpty()) {
            deleteImageButtons.get(0).click();
            waitForModalVisible();
            closeModal();
            log.info("✅ Первый эскиз удален");
        }
    }

    @Step("Удаление эскиза по ID")
    public void deleteSketchById(String sketchId) {
        try {
            WebElement deleteButton = driver.findElement(By.id("delete-" + sketchId));
            deleteButton.click();
            waitForModalVisible();
            closeModal();
            log.info("✅ Эскиз с ID {} удален", sketchId);
        } catch (Exception e) {
            log.warn("Не удалось удалить эскиз: {}", e.getMessage());
        }
    }

    @Step("Получение ID последнего эскиза")
    public String getLastSketchId() {
        try {
            if (!deleteImageButtons.isEmpty()) {
                return deleteImageButtons.get(0).getAttribute("id");
            }
            return null;
        } catch (Exception e) {
            log.warn("Не удалось получить ID эскиза: {}", e.getMessage());
            return null;
        }
    }

    @Step("Проверка доступности пагинации эскизов")
    public boolean isSketchesPaginationVisible() {
        try {
            return sketchesPerPageSelect.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Выбор количества эскизов на странице: {count}")
    public void selectSketchesPerPage(int count) {
        Select select = new Select(sketchesPerPageSelect);
        select.selectByValue(String.valueOf(count));
        log.info("✅ Выбрано {} эскизов на странице", count);
    }

    @Step("Проверка доступности кнопки следующей страницы эскизов")
    public boolean isSketchesNextPageEnabled() {
        try {
            return sketchesNextPageButton.isDisplayed() && sketchesNextPageButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Переход на следующую страницу эскизов")
    public void clickSketchesNextPage() {
        wait.until(ExpectedConditions.elementToBeClickable(sketchesNextPageButton)).click();
        log.info("➡️ Переход на следующую страницу эскизов");
    }

    @Step("Переход на предыдущую страницу эскизов")
    public void clickSketchesPrevPage() {
        wait.until(ExpectedConditions.elementToBeClickable(sketchesPrevPageButton)).click();
        log.info("⬅️ Переход на предыдущую страницу эскизов");
    }

    // ==================== МЕТОДЫ ОТЗЫВОВ ====================

    @Step("Получение количества отзывов")
    public int getReviewsCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(reviewCards));
            return reviewCards.size();
        } catch (Exception e) {
            log.warn("Не удалось получить отзывы: {}", e.getMessage());
            return 0;
        }
    }

    @Step("Удаление первого отзыва")
    public void deleteFirstReview() {
        wait.until(ExpectedConditions.visibilityOfAllElements(deleteReviewButtons));
        if (!deleteReviewButtons.isEmpty()) {
            deleteReviewButtons.get(0).click();
            waitForModalVisible();
            closeModal();
            log.info("✅ Первый отзыв удален");
        }
    }

    @Step("Получение текста первого отзыва")
    public String getFirstReviewText() {
        try {
            if (!reviewCards.isEmpty()) {
                return reviewCards.get(0).getText();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    // ==================== МЕТОДЫ НАСТРОЕК ====================

    @Step("Открытие вкладки 'Пользователи'")
    public void openUsersTab() {
        wait.until(ExpectedConditions.elementToBeClickable(usersTab)).click();
        log.info("✅ Открыта вкладка 'Пользователи'");
    }

    @Step("Открытие вкладки 'Контакты'")
    public void openContactTab() {
        wait.until(ExpectedConditions.elementToBeClickable(contactTab)).click();
        log.info("✅ Открыта вкладка 'Контакты'");
    }

    @Step("Открытие вкладки 'Хлебные крошки'")
    public void openBreadcrumbTab() {
        wait.until(ExpectedConditions.elementToBeClickable(breadcrumbTab)).click();
        log.info("✅ Открыта вкладка 'Хлебные крошки'");
    }

    @Step("Открытие вкладки 'Логотип'")
    public void openLogoTab() {
        wait.until(ExpectedConditions.elementToBeClickable(logoTab)).click();
        log.info("✅ Открыта вкладка 'Логотип'");
    }

    @Step("Получение количества пользователей")
    public int getUsersCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(userCards));
            return userCards.size();
        } catch (Exception e) {
            log.warn("Не удалось получить пользователей: {}", e.getMessage());
            return 0;
        }
    }

    @Step("Обновление контактной информации")
    public void updateContactInfo(String phone, String email, String address) {
        wait.until(ExpectedConditions.visibilityOf(phoneInput));

        phoneInput.clear();
        phoneInput.sendKeys(phone);

        emailInput.clear();
        emailInput.sendKeys(email);

        addressInput.clear();
        addressInput.sendKeys(address);

        clickPrimaryButton();
        log.info("✅ Контактная информация обновлена");
    }

    @Step("Получение значения поля телефона")
    public String getPhoneInputValue() {
        return phoneInput.getAttribute("value");
    }

    @Step("Получение значения поля email")
    public String getEmailInputValue() {
        return emailInput.getAttribute("value");
    }

    @Step("Получение значения поля адреса")
    public String getAddressInputValue() {
        return addressInput.getAttribute("value");
    }

    @Step("Загрузка логотипа")
    public void uploadLogo(String imagePath) {
        uploadFile(logoFileInput, imagePath);
        clickPrimaryButton();
        log.info("✅ Логотип загружен");
    }

    @Step("Получение src логотипа")
    public String getLogoSource() {
        try {
            WebElement logo = driver.findElement(By.cssSelector(".logo img, .navbar-brand img"));
            return logo.getAttribute("src");
        } catch (Exception e) {
            return null;
        }
    }

    @Step("Загрузка фона хлебных крошек")
    public void uploadBreadcrumbBackground(String imagePath) {
        uploadFile(breadcrumbFileInput, imagePath);
        clickPrimaryButton();
        log.info("✅ Фон хлебных крошек загружен");
    }

    @Step("Получение стиля фона хлебных крошек")
    public String getBreadcrumbBackgroundStyle() {
        try {
            WebElement breadcrumb = driver.findElement(By.cssSelector(".breadcrumb-section"));
            return breadcrumb.getCssValue("background-image");
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== МЕТОДЫ ОБНОВЛЕНИЯ РАЗДЕЛОВ ====================

    @Step("Обновление раздела 'Особенности'")
    public void updateFeatureSection(String text) {
        try {
            WebElement textArea = driver.findElement(By.cssSelector(".feature-import textarea"));
            textArea.clear();
            textArea.sendKeys(text);
            clickPrimaryButton();
            log.info("✅ Раздел 'Особенности' обновлен");
        } catch (Exception e) {
            log.error("Ошибка при обновлении раздела 'Особенности': {}", e.getMessage());
            throw e;
        }
    }

    @Step("Обновление раздела 'О нас'")
    public void updateAboutSection(String text) {
        try {
            WebElement textArea = driver.findElement(By.cssSelector(".home-about textarea"));
            textArea.clear();
            textArea.sendKeys(text);
            clickPrimaryButton();
            log.info("✅ Раздел 'О нас' обновлен");
        } catch (Exception e) {
            log.error("Ошибка при обновлении раздела 'О нас': {}", e.getMessage());
            throw e;
        }
    }

    @Step("Обновление раздела 'Классы'")
    public void updateClassesSection(String text) {
        try {
            WebElement textArea = driver.findElement(By.cssSelector(".classes textarea"));
            textArea.clear();
            textArea.sendKeys(text);
            clickPrimaryButton();
            log.info("✅ Раздел 'Классы' обновлен");
        } catch (Exception e) {
            log.error("Ошибка при обновлении раздела 'Классы': {}", e.getMessage());
            throw e;
        }
    }

    @Step("Обновление раздела 'Цены'")
    public void updatePriceSection(String text) {
        try {
            WebElement textArea = driver.findElement(By.cssSelector(".price input"));
            textArea.clear();
            textArea.sendKeys(text);
            clickPrimaryButton();
            log.info("✅ Раздел 'Цены' обновлен");
        } catch (Exception e) {
            log.error("Ошибка при обновлении раздела 'Цены': {}", e.getMessage());
            throw e;
        }
    }

    @Step("Обновление раздела 'Преимущества'")
    public void updateChooseUsSection(String text) {
        try {
            WebElement textArea = driver.findElement(By.cssSelector(".chooseus textarea"));
            textArea.clear();
            textArea.sendKeys(text);
            clickPrimaryButton();
            log.info("✅ Раздел 'Преимущества' обновлен");
        } catch (Exception e) {
            log.error("Ошибка при обновлении раздела 'Преимущества': {}", e.getMessage());
            throw e;
        }
    }

    // ==================== МЕТОДЫ ПРОВЕРКИ ВИДИМОСТИ РАЗДЕЛОВ ====================

    @Step("Проверка видимости раздела 'Особенности'")
    public boolean isFeatureSectionVisible() {
        try {
            return driver.findElement(By.cssSelector(".feature-import")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка видимости раздела 'О нас'")
    public boolean isAboutSectionVisible() {
        try {
            return driver.findElement(By.cssSelector(".home-about")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка видимости раздела 'Классы'")
    public boolean isClassesSectionVisible() {
        try {
            return driver.findElement(By.cssSelector(".classes")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка видимости раздела 'Цены'")
    public boolean isPriceSectionVisible() {
        try {
            return driver.findElement(By.cssSelector(".price")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверка видимости раздела 'Преимущества'")
    public boolean isChooseUsSectionVisible() {
        try {
            return driver.findElement(By.cssSelector(".chooseus")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private void uploadFile(WebElement fileInput, String filePath) {
        fileInput.sendKeys(filePath);
        wait.until(ExpectedConditions.attributeToBeNotEmpty(fileInput, "value"));
        log.info("✅ Файл загружен: {}", filePath);
    }

    private void clickPrimaryButton() {
        if (!primaryButtons.isEmpty()) {
            WebElement button = primaryButtons.get(0);
            wait.until(ExpectedConditions.elementToBeClickable(button)).click();
            log.info("✅ Нажата кнопка сохранения");
        }
    }

    @Step("Получение сообщения об успехе")
    public String getSuccessMessage() {
        try {
            waitForModalVisible();
            return successMessage.getText();
        } catch (Exception e) {
            log.debug("Сообщение об успехе не найдено");
            return "";
        }
    }

    @Step("Получение сообщения об ошибке")
    public String getErrorMessage() {
        try {
            waitForModalVisible();
            return errorMessage.getText();
        } catch (Exception e) {
            log.debug("Сообщение об ошибке не найдено");
            return "";
        }
    }

    @Step("Закрытие модального окна")
    public void closeModal() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(closeModalButton)).click();
            wait.until(ExpectedConditions.invisibilityOf(modal));
            log.info("✅ Модальное окно закрыто");
        } catch (Exception e) {
            log.debug("Модальное окно уже закрыто или не отображалось");
        }
    }
}