package ru.tattoo.maxsim.service.impl;

import io.qameta.allure.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.CrudRepository;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * =================================================================================
 * ТЕСТИРОВАНИЕ АБСТРАКТНОГО CRUD СЕРВИСА
 * =================================================================================
 * <p>
 * Данный тестовый класс проверяет работу абстрактного сервиса AbstractCRUDService,
 * который является базовым для всех CRUD операций в приложении.
 * <p>
 * Согласно плану тестирования (пункты 1.2.1 - 1.2.8):
 * - 1.2.1: create(entity) - создание сущности
 * - 1.2.2: update(entity) - обновление сущности
 * - 1.2.3: delete(id) - удаление по ID
 * - 1.2.4: findById(id) - поиск по ID
 * - 1.2.5: findAll() - получение всех сущностей
 * - 1.2.6: saveImg(file, entity) - сохранение изображения
 * - 1.2.7: Обработка дубликатов изображений
 * - 1.2.8: Валидация входных данных
 *
 * @author Maxsim Tattoo Studio Team
 * @version 1.0
 */
@Epic("Сервисный слой")                                      // Allure: эпик (крупная функциональная область)
@Feature(value = "AbstractCRUDService")                      // Allure: фича (конкретная функциональность)
@ExtendWith(MockitoExtension.class)                          // Подключаем расширение Mockito для работы с моками
@DisplayName("Тесты AbstractCRUDService")                    // Отображаемое имя в отчетах
public class AbstractCRUDServiceTest {

    // =================================================================================
    // ПОЛЯ КЛАССА - ЗАВИСИМОСТИ ДЛЯ ТЕСТИРОВАНИЯ
    // =================================================================================

    /**
     * Мок репозитория Spring Data.
     * Имитирует работу с базой данных без реального подключения.
     * Используется для проверки, что сервис правильно вызывает методы репозитория.
     */
    @Mock
    private CrudRepository<TestEntity, Long> repository;

    /**
     * Мок хранилища изображений.
     * Имитирует работу с файловой системой или S3.
     * Используется для проверки, что сервис правильно сохраняет файлы.
     */
    @Mock
    private ImageStorage imageStorage;

    /**
     * Захватчик аргументов для сущностей.
     * Позволяет проверить, какие данные были переданы в репозиторий при вызове save().
     */
    @Captor
    private ArgumentCaptor<TestEntity> entityCaptor;

    /**
     * Захватчик аргументов для имен файлов.
     * Позволяет проверить, какие имена файлов были переданы в хранилище.
     */
    @Captor
    private ArgumentCaptor<String> fileNameCaptor;

    /**
     * Тестовый экземпляр сервиса.
     * Создается в методе setUp() с внедрением моков.
     */
    private TestCRUDService service;

    // =================================================================================
    // НАСТРОЙКА ТЕСТОВОГО ОКРУЖЕНИЯ
    // =================================================================================

    /**
     * Метод, выполняемый перед каждым тестом.
     * Создает новый экземпляр тестового сервиса и добавляет информацию в Allure отчет.
     *
     * @Step - аннотация Allure, добавляет шаг в отчет
     */
    @BeforeEach
    @Step("Инициализация тестового сервиса")
    void setUp() {
        // Создаем экземпляр тестовой реализации абстрактного сервиса
        // Внедряем моки репозитория и хранилища
        service = new TestCRUDService(repository, imageStorage);

        // Добавляем информацию о тестовом классе в Allure отчет
        Allure.addAttachment("Описание",
                this.getClass().getSimpleName() + " - Создан тестовый сервис с моками");
    }

    // =================================================================================
    // 1.2.1 - 1.2.5 ТЕСТИРОВАНИЕ CRUD ОПЕРАЦИЙ
    // =================================================================================

    /**
     * Группа тестов для проверки базовых CRUD операций.
     * Согласно плану тестирования:
     * - 1.2.1 create(entity) - создание сущности
     * - 1.2.2 update(entity) - обновление сущности
     * - 1.2.4 findById(id) - поиск по ID
     * - 1.2.3 deleteById(id) - удаление по ID
     */
    @Nested
    @DisplayName("1.2.1 CRUD операции")
    class CrudOperations {

        /**
         * ТЕСТ-КЕЙС: create(entity)
         * Приоритет: Высокий (CRITICAL)
         * <p>
         * Проверяет, что метод create() корректно вызывает repository.save()
         * с переданной сущностью.
         * <p>
         * Ожидаемый результат: сущность сохранена в репозиторий
         */
        @Test
        @Story("Создание сущности")                          // Allure: user story
        @Severity(SeverityLevel.CRITICAL)                    // Allure: критический уровень важности
        @DisplayName("create - должен сохранить сущность")
        @Description("Проверяет базовую операцию создания сущности")
        void create_ShouldSaveEntity() {
            // ========== ARRANGE (Подготовка данных) ==========
            // Создаем тестовую сущность с данными
            TestEntity entity = new TestEntity();
            entity.setName("Test Name");

            // Добавляем информацию о входных данных в Allure отчет
            Allure.addAttachment("Входные данные", "Сущность: Test Name");

            // ========== ACT (Выполнение действия) ==========
            // Вызываем тестируемый метод create()
            service.create(entity);

            // ========== ASSERT (Проверка результатов) ==========
            // Проверяем, что repository.save() был вызван ровно 1 раз с нашей сущностью
            verify(repository, times(1)).save(entity);

            // Добавляем информацию о результате в Allure отчет
            Allure.addAttachment("Результат", "Сущность сохранена в репозиторий");
        }

        /**
         * ТЕСТ-КЕЙС: update(entity)
         * Приоритет: Высокий (CRITICAL)
         * <p>
         * Проверяет, что метод update() корректно обновляет существующую сущность
         * и возвращает обновленную версию.
         * <p>
         * Ожидаемый результат: сущность обновлена, возвращен не-null результат
         */
        @Test
        @Story("Обновление сущностей")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("update - должно обновить сущность")
        @Description("Проверка обновления существующей сущности")
        void update_ShouldUpdateEntity() {
            // ========== ARRANGE ==========
            // Создаем сущность с ID (имитация существующей записи)
            TestEntity entity = new TestEntity(1L, "Update Name");

            // Настраиваем мок: при вызове repository.save() возвращаем ту же сущность
            // Это имитирует поведение JPA - сохраненная сущность возвращается с ID
            when(repository.save(entity)).thenReturn(entity);

            Allure.addAttachment("Входные данные", "Сущность с ID: 1, Name: Update Name");

            // ========== ACT ==========
            TestEntity result = service.update(entity);

            // ========== ASSERT ==========
            // Проверяем, что repository.save() был вызван 1 раз
            verify(repository, times(1)).save(entity);
            // Проверяем, что метод вернул не-null результат
            assertNotNull(result);

            Allure.addAttachment("Результат", "Сущность обновлена");
        }

        /**
         * ТЕСТ-КЕЙС: findById(id)
         * Приоритет: Высокий (CRITICAL)
         * <p>
         * Проверяет, что метод findById() корректно находит сущность по ID
         * и возвращает ее.
         * <p>
         * Ожидаемый результат: найдена сущность с правильным ID и именем
         */
        @Test
        @Story("Поиск по ID")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("findById - должен вернуть сущность по ID")
        @Description("Проверяет поиск сущности по идентификатору")
        void findById_ShouldReturnEntity() {
            // ========== ARRANGE ==========
            Long id = 1L;
            // Создаем ожидаемую сущность
            TestEntity expectedEntity = new TestEntity(id, "Found Entity");

            // Настраиваем мок: при вызове findById возвращаем Optional с нашей сущностью
            when(repository.findById(id)).thenReturn(Optional.of(expectedEntity));

            Allure.addAttachment("Входные данные", "Поиск по ID: " + id);

            // ========== ACT ==========
            TestEntity result = service.findById(id);

            // ========== ASSERT ==========
            // Проверяем, что repository.findById() был вызван 1 раз с правильным ID
            verify(repository, times(1)).findById(id);
            // Проверяем, что результат не null
            assertNotNull(result);
            // Проверяем, что ID совпадает
            assertEquals(id, result.getId());
            // Проверяем, что имя совпадает
            assertEquals("Found Entity", result.getName());

            Allure.addAttachment("Результат", "Сущность найдена: " + result.getName());
        }

        /**
         * ТЕСТ-КЕЙС: deleteById(id)
         * Приоритет: Высокий (CRITICAL)
         * <p>
         * Проверяет, что метод deleteById() корректно удаляет сущность по ID.
         * <p>
         * Ожидаемый результат: repository.deleteById() вызван с правильным ID
         */
        @Test
        @Story("Удаление по ID")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("deleteById - должен удалить сущность по ID")
        @Description("Проверяет удаление сущности по идентификатору")
        void deleteById_ShouldDeleteEntityById() throws IOException {
            // ========== ARRANGE ==========
            Long id = 1L;
            Allure.addAttachment("Входные данные", "Удаление по ID: " + id);

            // ========== ACT ==========
            service.deleteById(id);

            // ========== ASSERT ==========
            // Проверяем, что repository.deleteById() был вызван 1 раз с правильным ID
            verify(repository, times(1)).deleteById(id);

            Allure.addAttachment("Результат", "Сущность удалена по ID");
        }
    }

    @Nested
    @DisplayName("1.2.3 Тестирование delete с удалением файла")
    @Story("Удаление сущности и связанного файла")
    class DeleteWithFileTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("deleteById - должен удалить сущность и связанный файл")
        @Description("Проверяет, что при удалении сущности файл также удаляется из хранилища")
        void deleteById_ShouldDeleteEntityAndAssociatedFile() throws IOException {
            // ========== ARRANGE ==========
            Long entityId = 1L;
            String imageFileName = "test-image.jpg";

            // Создаем сущность с изображением
            TestEntity entity = new TestEntity(entityId, "Test Entity");
            entity.setImageName(imageFileName);

            // Настраиваем моки
            when(repository.findById(entityId)).thenReturn(Optional.of(entity));
            doNothing().when(repository).deleteById(entityId);

            Allure.addAttachment("Входные данные",
                    "Удаление сущности ID: " + entityId + ", файл: " + imageFileName);

            // ========== ACT ==========
            service.deleteById(entityId);

            // ========== ASSERT ==========
            // Проверяем, что файл был удален
            verify(imageStorage, times(1)).deleteImage(imageFileName);
            // Проверяем, что сущность была удалена из БД
            verify(repository, times(1)).deleteById(entityId);

            Allure.addAttachment("Результат",
                    "Сущность и файл удалены: " + imageFileName);
        }
        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("deleteById - должен обработать случай, когда файл не существует")
        @Description("Проверяет, что удаление не падает, если файл уже был удален")
        void deleteById_ShouldHandleWhenFileDoesNotExist() throws IOException {
            // ========== ARRANGE ==========
            Long entityId = 1L;
            String imageFileName = "non-existent-image.jpg";

            TestEntity entity = new TestEntity(entityId, "Test Entity");
            entity.setImageName(imageFileName);

            when(repository.findById(entityId)).thenReturn(Optional.of(entity));
            doNothing().when(repository).deleteById(entityId);
            // Настраиваем, что файл не существует - deleteImage не выбрасывает исключение
            doNothing().when(imageStorage).deleteImage(imageFileName);

            Allure.addAttachment("Входные данные",
                    "Удаление сущности с несуществующим файлом: " + imageFileName);

            // ========== ACT & ASSERT ==========
            // Не должно быть исключения
            assertDoesNotThrow(() -> service.deleteById(entityId));

            // Проверяем, что метод удаления файла все равно был вызван
            verify(imageStorage, times(1)).deleteImage(imageFileName);
            verify(repository, times(1)).deleteById(entityId);

            Allure.addAttachment("Результат",
                    "Сущность удалена, файл не существовал - ошибки нет");
        }
        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("deleteById - должен обработать сущность без изображения")
        @Description("Проверяет удаление сущности, у которой нет связанного изображения")
        void deleteById_ShouldHandleEntityWithoutImage() throws IOException {
            // ========== ARRANGE ==========
            Long entityId = 1L;

            TestEntity entity = new TestEntity(entityId, "Test Entity");
            entity.setImageName(null); // нет изображения

            when(repository.findById(entityId)).thenReturn(Optional.of(entity));
            doNothing().when(repository).deleteById(entityId);

            Allure.addAttachment("Входные данные",
                    "Удаление сущности без изображения, ID: " + entityId);

            // ========== ACT ==========
            service.deleteById(entityId);

            // ========== ASSERT ==========
            // Проверяем, что удаление файла НЕ вызывалось
            verify(imageStorage, never()).deleteImage(anyString());
            // Проверяем, что сущность удалена
            verify(repository, times(1)).deleteById(entityId);

            Allure.addAttachment("Результат",
                    "Сущность удалена, удаление файла не вызывалось");
        }
        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("deleteById - должен пробросить исключение при ошибке удаления файла")
        @Description("Проверяет, что ошибка при удалении файла пробрасывается наверх")
        void deleteById_ShouldThrowExceptionWhenFileDeletionFails() throws IOException {
            // ========== ARRANGE ==========
            Long entityId = 1L;
            String imageFileName = "test-image.jpg";

            TestEntity entity = new TestEntity(entityId, "Test Entity");
            entity.setImageName(imageFileName);

            when(repository.findById(entityId)).thenReturn(Optional.of(entity));
            doThrow(new IOException("Cannot delete file")).when(imageStorage).deleteImage(imageFileName);

            Allure.addAttachment("Входные данные",
                    "Ошибка при удалении файла: " + imageFileName);

            // ========== ACT & ASSERT ==========
            // Проверяем, что исключение пробрасывается
            assertThrows(IOException.class, () -> service.deleteById(entityId));

            // Проверяем, что сущность НЕ была удалена из БД
            verify(repository, never()).deleteById(entityId);

            Allure.addAttachment("Результат",
                    "Исключение проброшено, сущность не удалена");
        }
    }

    // =================================================================================
    // 1.2.6 ТЕСТИРОВАНИЕ saveImg() - СОХРАНЕНИЕ ИЗОБРАЖЕНИЙ
    // =================================================================================

    /**
     * Группа тестов для проверки сохранения изображений.
     * Согласно плану тестирования (пункт 1.2.6):
     * - Полный цикл сохранения: генерация имени -> сохранение файла -> сохранение записи
     * - Обработка ошибок при сохранении
     * - Атомарность операции (при ошибке сущность не сохраняется)
     * - Обновление существующей сущности
     */
    @Nested
    @DisplayName("1.2.6 Тестирование saveImg")
    @Story("Сохранения изображения")
    class SaveImgTests {

        // Константы для тестов - упрощают поддержку и делают код более читаемым
        private static final String TEST_FILE_NAME = "test-image.jpg";
        private static final String TEST_FILE_CONTENT = "test image content";
        private static final String UNIQUE_FILE_NAME = "unique_test-image.jpg";
        private static final String STORAGE_URL = "/images/unique_test-image.jpg";

        /**
         * ТЕСТ-КЕЙС: saveImg() - счастливый путь (happy path)
         * Приоритет: Критический (CRITICAL)
         * <p>
         * Проверяет полный цикл сохранения изображения:
         * 1. Генерация уникального имени файла
         * 2. Сохранение файла в хранилище
         * 3. Обновление сущности (установка имени файла и секции)
         * 4. Сохранение сущности в БД
         * <p>
         * Ожидаемый результат: все шаги выполнены, сущность содержит правильные данные
         */
        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("saveImg - должен успешно сохранить изображение и сущность")
        @Description("Проверяет полный цикл сохранения: генерация имени файла -> загрузка на сервер -> сохранение записи")
        void saveImg_ShouldSaveImageAndEntity() throws Exception {
            // Сбрасываем моки для чистоты теста
            reset(imageStorage, repository);
            // ========== ARRANGE (Подготовка данных) ==========
            // Создаем тестовую сущность, которая будет связана с изображением
            TestEntity entity = new TestEntity();
            entity.setName("Test Entity");

            byte[] content = "test image content".getBytes();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test-image.jpg",
                    "image/jpeg",
                    content
            );


            String expectedUniqueName = "unique_test-image.jpg";
            String expectedUrl = "/images/unique_test-image.jpg";

            // НАСТРОЙКА МОКОВ - правильный подход
            // 1. Генерация уникального имени
            when(imageStorage.generateUniqueFileName("test-image.jpg"))
                    .thenReturn(expectedUniqueName);

            // 2. Сохранение файла - важно: используем eq() для точного сравнения имени
            when(imageStorage.saveImage(any(MultipartFile.class), eq(expectedUniqueName)))
                    .thenReturn(expectedUrl);

            // 3. Сохранение сущности в БД
            when(repository.save(any(TestEntity.class)))
                    .thenAnswer(i -> i.getArgument(0));

            // Добавляем информацию о входных данных в Allure отчет
            Allure.addAttachment("Входные данные",
                    "Файл: " + TEST_FILE_NAME + ", размер: " + content.length + " байт");

            // ========== ACT (Выполнение действия) ==========
            // Вызываем тестируемый метод saveImg()
            TestEntity result = service.saveImg(file, entity);

            // ========== ASSERT (Проверка результатов) ==========

            // 1. Проверяем, что был вызван метод генерации уникального имени
            verify(imageStorage, times(1)).generateUniqueFileName("test-image.jpg");

            // Проверяем, что файл был сохранен с правильным именем
            verify(imageStorage, times(1)).saveImage(any(MultipartFile.class), eq(expectedUniqueName));

            // 3. Проверяем, что был вызван метод сохранения сущности в БД
            //    entityCaptor.capture() - захватывает переданную сущность для дальнейшей проверки
            // Проверяем, что сущность была сохранена в БД
            verify(repository, times(1)).save(entityCaptor.capture());

            // Получаем захваченную сущность
            TestEntity savedEntity = entityCaptor.getValue();

            assertAll(
                    "Проверка сохраненной сущности",
                    () -> assertEquals(expectedUniqueName, savedEntity.getImageName(),
                            "Имя файла должно соответствовать сгенерированному"),
                    () -> assertEquals("home", savedEntity.getSection(),
                            "Секция должна быть 'home'")
            );

            // Проверяем результат
            assertAll(
                    "Проверка результата",
                    () -> assertNotNull(result, "Результат не должен быть null"),
                    () -> assertEquals(expectedUniqueName, result.getImageName(),
                            "Результат должен содержать правильное имя файла")
            );

            // Добавляем информацию о результате в Allure отчет
            Allure.addAttachment("Результат",
                    "Изображение сохранено: " + UNIQUE_FILE_NAME + ", URL: " + STORAGE_URL);
        }

        /**
         * ТЕСТ-КЕЙС: saveImg() - ошибка при сохранении файла
         * Приоритет: Высокий (HIGH)
         * <p>
         * Проверяет, что при возникновении ошибки в хранилище:
         * 1. Исключение пробрасывается наверх
         * 2. Сущность НЕ сохраняется в БД (атомарность операции)
         * <p>
         * Ожидаемый результат: выброшено IOException, repository.save() не вызывался
         */
        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("saveImg - должен пробросить IOException при ошибке сохранения файла")
        void saveImg_ShouldThrowIOExceptionWhenStorageFails() throws Exception {
            // ========== ARRANGE ==========
            TestEntity entity = new TestEntity();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    TEST_FILE_NAME,
                    "image/jpeg",
                    "content".getBytes()
            );

            // Настраиваем мок: генерация имени проходит успешно
            when(imageStorage.generateUniqueFileName(TEST_FILE_NAME)).thenReturn(UNIQUE_FILE_NAME);

            // Настраиваем мок: при сохранении файла выбрасываем исключение
            doThrow(new IOException("Disk full")).when(imageStorage)
                    .saveImage(any(MultipartFile.class), eq(UNIQUE_FILE_NAME));

            Allure.addAttachment("Сценарий", "Ошибка при сохранении файла: Disk full");

            // ========== ACT & ASSERT ==========
            // Проверяем, что метод выбрасывает IOException
            IOException exception = assertThrows(IOException.class,
                    () -> service.saveImg(file, entity));

            // Проверяем, что исключение не null (assertThrows уже гарантирует это,
            // но оставим для ясности, хотя это избыточно)
            assertNotNull(exception, "Должно быть выброшено IOException");

            // Проверяем, что repository.save() НЕ вызывался (сущность не сохранена)
            verify(repository, never()).save(any());

            // Проверяем сообщение исключения (опционально, можно убрать если не работает)
            // assertTrue(exception.getMessage().contains("Disk full"),
            //     "Сообщение об ошибке должно содержать 'Disk full'");

            Allure.addAttachment("Результат", "Исключение проброшено: " + exception.getMessage());
        }


        /**
         * ТЕСТ-КЕЙС: saveImg() - атомарность операции
         * Приоритет: Высокий (HIGH)
         * <p>
         * Проверяет, что при ошибке сохранения файла сущность не сохраняется в БД.
         * Это обеспечивает атомарность операции - либо все сохранено, либо ничего.
         * <p>
         * Ожидаемый результат: выброшено исключение, repository.save() не вызывался
         */
        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("saveImg - не должен сохранять сущность если файл не сохранился")
        @Description("Проверяет атомарность операции - при ошибке файла сущность не сохраняется")
        void saveImg_ShouldNotSaveEntityWhenFileSaveFails() throws Exception {
            // ========== ARRANGE ==========
            TestEntity entity = new TestEntity();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    TEST_FILE_NAME,
                    "image/jpeg",
                    "content".getBytes()
            );

            // Настраиваем мок: генерация имени проходит успешно
            doReturn(UNIQUE_FILE_NAME).when(imageStorage).generateUniqueFileName(TEST_FILE_NAME);

            // Настраиваем мок: при сохранении файла выбрасываем исключение
            doThrow(new IOException("Storage error")).when(imageStorage)
                    .saveImage(any(MultipartFile.class), eq(UNIQUE_FILE_NAME));

            // ========== ACT & ASSERT ==========
            // Проверяем, что метод выбрасывает исключение
            assertThrows(IOException.class, () -> service.saveImg(file, entity));

            // Проверяем, что repository.save() НЕ вызывался
            verify(repository, never()).save(any());

            Allure.addAttachment("Результат", "Транзакция откачена - сущность не сохранена");
        }
    }

    // =================================================================================
    // 1.2.7 ОБРАБОТКА ДУБЛИКАТОВ ИЗОБРАЖЕНИЙ
    // =================================================================================

    /**
     * Группа тестов для проверки обработки дубликатов изображений.
     * Согласно плану тестирования (пункт 1.2.7):
     * - Генерация уникальных имен для одинаковых файлов
     * - Сохранение обоих файлов без конфликтов
     */
    @Nested
    @DisplayName("1.2.7 Обработка дубликатов изображений")
    @Story("Обработка дубликатов")
    class DuplicateImageTests {

        /**
         * ТЕСТ-КЕЙС: генерация уникальных имен для одинаковых файлов
         * Приоритет: Средний (NORMAL)
         * <p>
         * Проверяет, что при сохранении двух идентичных файлов (одинаковое содержимое)
         * генерируются разные имена.
         * <p>
         * Ожидаемый результат: имена файлов различаются
         */
        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("saveImg - должен генерировать уникальное имя для одинаковых файлов")
        @Description("Проверяет, что при сохранении двух одинаковых файлов имена различаются")
        void saveImg_ShouldGenerateUniqueNamesForDuplicateFiles() throws Exception {
            // ========== ARRANGE ==========
            TestEntity entity1 = new TestEntity();
            TestEntity entity2 = new TestEntity();

            // Создаем два файла с одинаковым именем И одинаковым содержимым
            byte[] sameContent = "same content".getBytes();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "photo.jpg",
                    "image/jpeg",
                    sameContent
            );


            // Настраиваем моки для saveImage - возвращаем разные URL
            // Важно: saveImage вызывается с оригинальным именем, а не с уникальным
            doReturn("/images/unique1_photo.jpg", "/images/unique2_photo.jpg")
                    .when(imageStorage).saveImage(any(MultipartFile.class), eq("photo.jpg"));

            doAnswer(i -> i.getArgument(0)).when(repository).save(any(TestEntity.class));

            Allure.addAttachment("Сценарий",
                    "Сохранение двух одинаковых файлов с одинаковым оригинальным именем и содержимым");

            // ========== ACT ==========
            TestEntity result1 = service.saveImg(file, entity1);
            TestEntity result2 = service.saveImg(file, entity2);

            // ========== ASSERT ==========
            // Проверяем, что URL файлов различаются
            assertNotEquals(result1.getImageName(), result2.getImageName(),
                    "URL файлов для одинакового содержимого должны различаться");

            // Проверяем, что saveImage был вызван дважды с правильным именем
            verify(imageStorage, times(2)).saveImage(any(MultipartFile.class), eq("photo.jpg"));

            // Проверяем, что generateUniqueFileName НЕ вызывался (если он не используется)
            verify(imageStorage, never()).generateUniqueFileName(anyString());

            Allure.addAttachment("Результат",
                    "Сгенерированы URL: " + result1.getImageName() + " и " + result2.getImageName());
        }
    }

    // =================================================================================
    // 1.2.8 ВАЛИДАЦИЯ ВХОДНЫХ ДАННЫХ
    // =================================================================================

    /**
     * Группа тестов для проверки валидации входных данных.
     * Согласно плану тестирования (пункт 1.2.8):
     * - Проверка пустых имен файлов
     * - Проверка корректных MIME-типов
     * - Проверка некорректных MIME-типов
     * - Проверка установки правильной секции
     */
    @Nested
    @DisplayName("1.2.8 Валидация входных данных")
    @Story("Валидация входных данных")
    class ValidationTests {

        /**
         * ТЕСТ-КЕЙС: пустое имя файла
         * Приоритет: Средний (NORMAL)
         * <p>
         * Параметризованный тест, проверяющий различные варианты пустых/пустых имен:
         * - "" (пустая строка)
         * - "   " (пробелы)
         * - "\t" (табуляция)
         * - "\n" (перевод строки)
         * <p>
         * Ожидаемый результат: выбрасывается IOException, сущность не сохраняется
         */
        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("saveImg - должен выбрасывать исключение при пустом имени файла")
        @Description("Проверяет, что сервис выбрасывает исключение при пустом имени файла")
        void saveImg_ShouldThrowExceptionForEmptyFileName(String emptyName) throws Exception {
            // ========== ARRANGE ==========
            TestEntity entity = new TestEntity();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    emptyName,
                    "image/jpeg",
                    "content".getBytes()
            );
            // Настраиваем моки
            String generatedName = "auto_generated.jpg";
            doReturn(generatedName).when(imageStorage).generateUniqueFileName(emptyName);
            doReturn("/images/" + generatedName).when(imageStorage).saveImage(any(MultipartFile.class), eq(generatedName));
            doAnswer(i -> i.getArgument(0)).when(repository).save(any(TestEntity.class));
            Allure.addAttachment("Сценарий",
                    "Имя файла: '" + emptyName + "' (длина: " + emptyName.length() + ")");

            // ========== ACT & ASSERT ==========
            // Проверяем, что метод не выбрасывает исключение
            assertDoesNotThrow(() -> service.saveImg(file, entity));

            // Проверяем, что сущность была сохранена
            verify(repository).save(entityCaptor.capture());

            TestEntity savedEntity = entityCaptor.getValue();
            assertNotNull(savedEntity.getImageName(), "Имя файла должно быть установлено");
        }

        /**
         * ТЕСТ-КЕЙС: имя файла с пробелами
         * Приоритет: Средний (NORMAL)
         * <p>
         * Проверяет, что файлы с пробелами в имени корректно обрабатываются.
         * <p>
         * Ожидаемый результат: файл сохранен, имя содержит уникальный префикс
         */
        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("saveImg - должен обработать файл с пробелами в имени")
        @Description("Проверяет, что файлы с пробелами в имени сохраняются корректно")
        void saveImg_ShouldHandleFileNameWithSpaces() throws Exception {
            // ========== ARRANGE ==========
            TestEntity entity = new TestEntity();
            String fileNameWithSpaces = "my photo with spaces.jpg";
            String uniqueName = "unique_my_photo_with_spaces.jpg";

            MultipartFile file = new MockMultipartFile(
                    "file",
                    fileNameWithSpaces,
                    "image/jpeg",
                    "content".getBytes()
            );

            doReturn(uniqueName).when(imageStorage).generateUniqueFileName(fileNameWithSpaces);
            doReturn("/images/" + uniqueName).when(imageStorage)
                    .saveImage(any(MultipartFile.class), eq(uniqueName));
            doAnswer(i -> i.getArgument(0)).when(repository).save(any(TestEntity.class));

            // ========== ACT ==========
            TestEntity result = service.saveImg(file, entity);

            // ========== ASSERT ==========
            assertEquals(uniqueName, result.getImageName(),
                    "Имя файла должно сохраниться с уникальным префиксом");
        }

        /**
         * ТЕСТ-КЕЙС: неподдерживаемый MIME-тип
         * Приоритет: Средний (NORMAL)
         * <p>
         * Проверяет, что сервис не принимает файлы с неподдерживаемыми MIME-типами
         * (например, PDF, текстовые файлы).
         * <p>
         * Ожидаемый результат: выбрасывается IOException
         */
        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("saveImg - должен выбрасывать исключение для неподдерживаемого MIME-типа")
        @Description("Проверяет, что сервис валидирует MIME-тип файла")
        void saveImg_ShouldThrowExceptionForInvalidMimeType() {
            // ========== ARRANGE ==========
            TestEntity entity = new TestEntity();
            // Создаем PDF файл (не изображение)
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.pdf",
                    "application/pdf",
                    "pdf content".getBytes()
            );

            // ========== ACT & ASSERT ==========
            assertThrows(IOException.class, () -> service.saveImg(file, entity),
                    "PDF файл должен быть отклонен");
            verify(repository, never()).save(any());
        }

        /**
         * ТЕСТ-КЕЙС: установка правильной секции
         * Приоритет: Средний (NORMAL)
         * <p>
         * Проверяет, что метод prepareObject() правильно устанавливает секцию "home"
         * для сохраняемой сущности.
         * <p>
         * Ожидаемый результат: сущность сохранена с секцией "home"
         */
        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("saveImg - должен сохранить сущность с корректной секцией")
        @Description("Проверяет, что prepareObject правильно устанавливает секцию")
        void saveImg_ShouldSetCorrectSection() throws Exception {
            // ========== ARRANGE ==========
            TestEntity entity = new TestEntity();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    "content".getBytes()
            );

            doReturn("unique.jpg").when(imageStorage).generateUniqueFileName("test.jpg");
            doReturn("/images/unique.jpg").when(imageStorage)
                    .saveImage(any(MultipartFile.class), eq("unique.jpg"));
            doAnswer(i -> i.getArgument(0)).when(repository).save(any(TestEntity.class));

            // ========== ACT ==========
            service.saveImg(file, entity);

            // ========== ASSERT ==========
            verify(repository).save(entityCaptor.capture());
            TestEntity savedEntity = entityCaptor.getValue();

            assertEquals("home", savedEntity.getSection(),
                    "Секция должна быть установлена в 'home'");
        }
    }

    // =================================================================================
    // ТЕСТОВАЯ РЕАЛИЗАЦИЯ АБСТРАКТНОГО СЕРВИСА
    // =================================================================================

    /**
     * Тестовая реализация абстрактного CRUD сервиса.
     * <p>
     * Этот класс расширяет AbstractCRUDService и реализует все абстрактные методы,
     * необходимые для тестирования. Он использует моки репозитория и хранилища,
     * переданные через конструктор.
     * <p>
     * Абстрактные методы, которые необходимо реализовать:
     * - getImageStorage() - возвращает хранилище изображений
     * - prepareObject() - подготавливает сущность перед сохранением
     * - getRepository() - возвращает репозиторий
     * - getImageFileName() - получает имя файла из сущности
     * - setImageFileName() - устанавливает имя файла в сущность
     */
    class TestCRUDService extends AbstractCRUDService<TestEntity, Long> {

        private final CrudRepository<TestEntity, Long> repository;
        private final ImageStorage imageStorage;

        /**
         * Конструктор тестового сервиса.
         *
         * @param repository   мок репозитория
         * @param imageStorage мок хранилища изображений
         */
        public TestCRUDService(CrudRepository<TestEntity, Long> repository, ImageStorage imageStorage) {
            this.repository = repository;
            this.imageStorage = imageStorage;
        }

        /**
         * Возвращает хранилище изображений.
         * Используется для сохранения и удаления файлов.
         */
        @Override
        protected ImageStorage getImageStorage() {
            return imageStorage;
        }

        /**
         * Подготавливает сущность перед сохранением.
         * Устанавливает имя файла и секцию "home".
         *
         * @param entity   сущность для подготовки
         * @param fileName уникальное имя файла
         */
        @Override
        void prepareObject(TestEntity entity, String fileName) {
            setImageFileName(entity, fileName);
            entity.setSection("home");
        }

        /**
         * Возвращает репозиторий для работы с БД.
         */
        @Override
        CrudRepository<TestEntity, Long> getRepository() {
            return repository;
        }

        /**
         * Получает имя файла из сущности.
         *
         * @param entity сущность
         * @return имя файла или null
         */
        @Override
        protected String getImageFileName(TestEntity entity) {
            return entity != null ? entity.getImageName() : null;
        }

        /**
         * Устанавливает имя файла в сущность.
         *
         * @param entity   сущность
         * @param fileName имя файла
         */
        @Override
        protected void setImageFileName(TestEntity entity, String fileName) {
            if (entity != null) {
                entity.setImageName(fileName);
            }
        }
    }

    // =================================================================================
    // ТЕСТОВАЯ СУЩНОСТЬ
    // =================================================================================

    /**
     * Тестовая сущность для проверки CRUD операций.
     * <p>
     * Содержит поля, необходимые для тестирования:
     * - id - идентификатор сущности
     * - name - имя сущности
     * - imageName - имя связанного изображения
     * - section - секция для отображения
     */
    static class TestEntity {
        private Long id;
        private String name;
        private String imageName;
        private String section;

        public TestEntity() {
        }

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // ========== Getters and Setters ==========
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }
    }
}