package ru.tattoo.maxsim.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * Интерфейс для работы с хранилищем изображений.
 * Позволяет абстрагироваться от конкретного способа хранения.
 */
public interface ImageStorage {

    String saveImage(MultipartFile file, String fileName) throws IOException;

    void deleteImage(String fileName) throws IOException;

    boolean existsImage(String fileName);

    byte[] getImage(String fileName) throws IOException;

    String generateUniqueFileName(String originalFileName);

    /** Все имена файлов в хранилище (для очистки сирот). */
    List<String> listImages() throws IOException;

    /** Время последнего изменения файла. */
    Instant lastModified(String fileName) throws IOException;
}
