package ru.tattoo.maxsim.storage.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.storage.ImageStorage;
import ru.tattoo.maxsim.storage.Impl.InMemoryImageStorage;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты InMemoryImageStorage")  // Имя теста в отчетах
public class InMemoryImageStorageTest {
    private ImageStorage imageStorage;  // Поле для тестируемого объекта
    @BeforeEach
        // Выполняется ПЕРЕД каждым тестом
        //Метод setUp - подготовка перед каждым тестом
    void setUp() {
        // Создаем новый экземпляр InMemoryImageStorage для каждого теста
        // Это гарантирует, что тесты не влияют друг на друга
        imageStorage = new InMemoryImageStorage();
    }

    @Nested                     // Группирует связанные тесты
    @DisplayName("1.1.1 Сохранение файла")  // Название группы
    class SaveImageTests {

        @Test
        @DisplayName("Должен сохранить файл и вернуть имя")
        void shouldSaveFileAndReturnName() throws Exception {
            // Arrange (Подготовка) - создаем тестовые данные
            byte[] content = "test image content".getBytes();  // Содержимое файла
            MultipartFile file = new MockMultipartFile(        // Создаем мок-файл
                    "file",                         // Имя параметра в форме
                    "test-image.jpg",                // Оригинальное имя файла
                    "image/jpeg",                    // MIME-тип
                    content                          // Содержимое
            );

            // Act (Действие) - вызываем тестируемый метод
            String savedFileName = imageStorage.saveImage(file, "test-image.jpg");

            // Assert (Проверка) - проверяем результат
            assertNotNull(savedFileName);  // Имя файла не должно быть null
        }

        @Test
        @DisplayName("Должен выбросить исключение при пустом файле")
        void shouldThrowExceptionForEmptyFile() {
            // Arrange - создаем пустой файл
            MultipartFile emptyFile = new MockMultipartFile(
                    "file",
                    "empty.jpg",
                    "image/jpeg",
                    new byte[0]  // Пустой массив байт = пустой файл
            );

            // Act & Assert - проверяем что выбросилось исключение
            assertThrows(IllegalArgumentException.class,
                    () -> imageStorage.saveImage(emptyFile, "empty.jpg"));
            // assertThrows проверяет что метод выбросил указанное исключение
        }
    }
    @Nested
    @DisplayName("1.1.2 Получение файла")
    class GetImageTests {

        @Test
        @DisplayName("Должен получить сохраненный файл")
        void shouldGetSavedFile() throws Exception {
            // Arrange - сначала сохраняем файл
            byte[] content = "hello world".getBytes();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.txt",
                    "text/plain",
                    content
            );
            String savedName = imageStorage.saveImage(file, "test.txt");

            // Act - получаем файл по имени
            byte[] retrieved = imageStorage.getImage(savedName);

            // Assert - проверяем что содержимое совпадает
            assertArrayEquals(content, retrieved);  // Сравниваем массивы байт
        }

        @Test
        @DisplayName("Должен выбросить исключение для несуществующего файла")
        void shouldThrowForNonExistentFile() {
            // Проверяем что при попытке получить несуществующий файл
            // выбрасывается IOException
            assertThrows(IOException.class,
                    () -> imageStorage.getImage("non-existent.jpg"));
        }
    }

    @Nested
    @DisplayName("1.1.3 Удаление файла")
    class DeleteImageTests {

        @Test
        @DisplayName("Должен удалить существующий файл")
        void shouldDeleteExistingFile() throws Exception {
            // Arrange - сохраняем файл
            byte[] content = "to delete".getBytes();
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "delete-me.jpg",
                    "image/jpeg",
                    content
            );
            String savedName = imageStorage.saveImage(file, "delete-me.jpg");
            assertTrue(imageStorage.existsImage(savedName));  // Проверяем что файл есть

            // Act - удаляем файл
            imageStorage.deleteImage(savedName);

            // Assert - проверяем что файла больше нет
            assertFalse(imageStorage.existsImage(savedName));
        }

        @Test
        @DisplayName("Не должен падать при удалении несуществующего файла")
        void shouldNotThrowWhenDeletingNonExistentFile() {
            // Проверяем что удаление несуществующего файла
            // не выбрасывает исключение
            assertDoesNotThrow(() -> imageStorage.deleteImage("non-existent.jpg"));
        }
    }
    @Nested
    @DisplayName("1.1.4 Генерация UUID")
    class GenerateUniqueFileNameTests {

        @Test
        @DisplayName("Должен генерировать имя с UUID формата 8-4-4-4-12")
        void shouldGenerateNameWithUuidFormat() {
            // Act - генерируем имя
            String name = imageStorage.generateUniqueFileName("test.jpg");

            // Assert - проверяем формат UUID
            String uuidPart = name.split("_")[0];  // Берем часть до подчеркивания
            String[] groups = uuidPart.split("-");   // Разбиваем по дефисам

            // assertAll - группирует несколько проверок
            assertAll(
                    () -> assertEquals(5, groups.length, "Должно быть 5 групп"),
                    () -> assertEquals(8, groups[0].length(), "Первая группа 8 символов"),
                    () -> assertEquals(4, groups[1].length(), "Вторая группа 4 символа"),
                    () -> assertEquals(4, groups[2].length(), "Третья группа 4 символа"),
                    () -> assertEquals(4, groups[3].length(), "Четвертая группа 4 символа"),
                    () -> assertEquals(12, groups[4].length(), "Пятая группа 12 символов"),
                    () -> assertTrue(name.endsWith("_test.jpg"), "Должно заканчиваться на _test.jpg")
            );
        }

        @Test
        @DisplayName("Должен выбросить исключение для null")
        void shouldThrowForNull() {
            assertThrows(NullPointerException.class,
                    () -> imageStorage.generateUniqueFileName(null));
        }
    }
}
