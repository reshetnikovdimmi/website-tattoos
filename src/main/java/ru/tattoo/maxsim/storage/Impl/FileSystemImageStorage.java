package ru.tattoo.maxsim.storage.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Реализация для продакшена.
 * Сохраняет файлы в файловую систему.
 */

@Slf4j
public class FileSystemImageStorage  implements ImageStorage {

    private final Path uploadDirectory;
    
    /**
     * @param uploadPath путь к директории для сохранения файлов
     */

    public FileSystemImageStorage(String uploadPath) {
        this.uploadDirectory = Paths.get(uploadPath).normalize().toAbsolutePath();
        // Инициализируем директорию (создаем если нет)
        initDirectory();
    }

    /**
     * Создает директорию для загрузки, если она не существует
     */
    private void initDirectory() {
        try {
            // Files.createDirectories() - создает все недостающие директории в пути
            // Например, если нет папки uploads, создаст и uploads, и images
            Files.createDirectories(uploadDirectory);
            log.info("📁 Инициализирована директория загрузки: {}", uploadDirectory);
        } catch (IOException e){
            log.error("❌ Не удалось создать директорию: {}", uploadDirectory, e);
            // Пробрасываем RuntimeException, потому что без директории приложение не может работать
            throw new RuntimeException("Не удалось создать директорию для загрузки файлов", e);
        }
    }
    /**
     * Сохраняет изображение на диск
     * @param file файл от пользователя (MultipartFile из Spring)
     * @param fileName оригинальное имя файла
     * @return уникальное имя сохраненного файла
     */
    @Override
    public String saveImage(MultipartFile file, String fileName) throws Exception {
        // 1. Проверяем, что файл валидный
        validateFile(file);

        // 2. Генерируем уникальное имя файла (чтобы не перезаписывать существующие)
        String uniqueFileName = generateUniqueFileName(fileName);

        // 3. Создаем полный путь к файлу: /uploads/images/ + уникальное_имя
        Path filePath = uploadDirectory.resolve(uniqueFileName);

        // 4. Защита от path traversal атак (очень важно для безопасности!)
        // Например, если кто-то попытается сохранить файл как "../../etc/passwd"
        if (!filePath.normalize().startsWith(uploadDirectory)) {
            throw new SecurityException("Попытка path traversal: " + fileName);
        }

        // 5. Сохраняем файл на диск
        // file.getBytes() - получаем содержимое файла как массив байт
        // Files.write() - записываем байты в файл по указанному пути
        Files.write(filePath, file.getBytes());

        log.debug("✅ Файл сохранен: {}", uniqueFileName);

        // 6. Возвращаем уникальное имя для сохранения в БД
        return uniqueFileName;
    }

    @Override
    public void deleteImage(String fileName) throws IOException {
        // Проверяем, что имя файла не пустое
        if (fileName == null || fileName.isEmpty()) {
            return; // Ничего не делаем
        }

        // Создаем полный путь к файлу
        Path filePath = uploadDirectory.resolve(fileName);

        // Files.deleteIfExists() - удаляет файл, если он существует
        // Возвращает true, если файл был удален
        boolean deleted = Files.deleteIfExists(filePath);

        if (deleted) {
            log.debug("🗑️ Файл удален: {}", fileName);
        }
    }

    /**
     * Проверяет, существует ли файл на диске
     */
    @Override
    public boolean existsImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        // Files.exists() - проверяет существование файла
        return Files.exists(uploadDirectory.resolve(fileName));
    }
    /**
     * Получает содержимое файла как массив байт
     * Используется для отдачи файла через контроллер
     */
    @Override
    public byte[] getImage(String fileName) throws IOException {
        if (!existsImage(fileName)) {
            throw new IOException("Файл не найден: " + fileName);
        }
        // Files.readAllBytes() - читает весь файл в память
        // Для больших файлов нужно использовать стримы, но для изображений норм
        return Files.readAllBytes(uploadDirectory.resolve(fileName));
    }
    /**
     * Генерирует уникальное имя файла
     * Формат: UUID_оригинальноеИмя
     * Например: "123e4567-e89b-12d3-a456-426614174000_photo.jpg"
     */
    @Override
    public String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null) {
            throw new NullPointerException("Имя файла не может быть null");
        }

        // Paths.get(originalFileName).getFileName() - вырезает только имя файла из пути
        // Например: из "../../etc/passwd" получит просто "passwd"
        String cleanFileName = Paths.get(originalFileName).getFileName().toString();

        // UUID.randomUUID() - генерирует уникальный идентификатор
        // Конкатенируем с оригинальным именем через "_"
        return UUID.randomUUID() + "_" + cleanFileName;
    }

    @Override
    public <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null || list.isEmpty() || size <= 0) {
            return List.of(); // Возвращаем пустой список
        }
        return List.of(list); // Пока просто возвращаем список в списке
    }

    /**
     * Валидирует входной файл
     * @param file файл для проверки
     */
    private void validateFile(MultipartFile file) {
        // Проверка на null
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }
        // Проверка на пустой файл
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }
        // Здесь можно добавить проверку:
        // - размера файла (например, не больше 10MB)
        // - типа файла (только изображения)
        // - названия файла (нет спецсимволов)
    }
}
