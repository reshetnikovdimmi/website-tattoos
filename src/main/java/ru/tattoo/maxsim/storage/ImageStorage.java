package ru.tattoo.maxsim.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс для работы с хранилищем изображений.
 * Позволяет абстрагироваться от конкретного способа хранения.
 */
public interface ImageStorage {

    /**
     * Сохраняет изображение
     *
     * @param file     файл для сохранения
     * @param fileName имя файла
     * @return уникальный идентификатор сохраненного файла
     */
    String saveImage(MultipartFile file, String fileName) throws Exception;

    /**
     * Удаляет изображение
     * @param fileName имя файла
     */
    void deleteImage(String fileName) throws IOException;

    /**
     * Проверяет существование изображения
     * @param fileName имя файла
     */
    boolean existsImage(String fileName);

    /**
     * Получает изображение как массив байт
     * @param fileName имя файла
     */
    byte[] getImage(String fileName) throws IOException;

    /**
     * Генерирует уникальное имя файла
     * @param originalFileName оригинальное имя
     */
    String generateUniqueFileName(String originalFileName);

    /**
     * Разбивает список на страницы
     */
    <T> List<List<T>> partition(List<T> list, int size);
}
