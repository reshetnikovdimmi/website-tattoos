package ru.tattoo.maxsim.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import java.util.UUID;


import org.apache.commons.collections4.ListUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtils {
    private static final Path UPLOAD_DIRECTORY = Paths.get("/app/img/images");  // Путь внутри контейнера

    /**
     * Сохраняет изображение на сервере.
     */
    public static void saveImage(MultipartFile file, String fileName) throws IOException {
        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
        Files.write(filePath, file.getBytes());
    }

    /**
     * Удаляет изображение с сервера.
     */
    public static void deleteImage(String fileName) throws IOException {
        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
        Files.deleteIfExists(filePath);
    }

    /**
     * Проверяет наличие изображения на сервере.
     */
    public static boolean existsImage(String fileName) {
        Path filePath = UPLOAD_DIRECTORY.resolve(fileName);
        return Files.exists(filePath);
    }

    /**
     * Генерация уникального имени файла.
     */
    public static String generateUniqueFileName(String originalFileName) {
        // Логика генерации уникального имени файла
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    /**
     * Разбивает список изображений на страницы.
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return ListUtils.partition(list, size);
    }
}
