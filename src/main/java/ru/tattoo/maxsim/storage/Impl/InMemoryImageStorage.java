package ru.tattoo.maxsim.storage.Impl;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemory реализация для тестирования.
 * Хранит файлы в памяти, не трогает диск.
 */
public class InMemoryImageStorage implements ImageStorage {

    private record StoredImage(byte[] content, Instant modifiedAt) {
    }

    private final Map<String, StoredImage> storage = new ConcurrentHashMap<>();

    @Override
    public String saveImage(MultipartFile file, String fileName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        String uniqueName = generateUniqueFileName(fileName);
        storage.put(uniqueName, new StoredImage(file.getBytes(), Instant.now()));
        return uniqueName;
    }

    @Override
    public void deleteImage(String fileName) {
        storage.remove(fileName);
    }

    @Override
    public boolean existsImage(String fileName) {
        return storage.containsKey(fileName);
    }

    @Override
    public byte[] getImage(String fileName) throws IOException {
        StoredImage stored = storage.get(fileName);
        if (stored == null) {
            throw new IOException("Файл не найден: " + fileName);
        }
        return stored.content();
    }

    @Override
    public Instant lastModified(String fileName) throws IOException {
        StoredImage stored = storage.get(fileName);
        if (stored == null) {
            throw new IOException("Файл не найден: " + fileName);
        }
        return stored.modifiedAt();
    }

    @Override
    public List<String> listImages() {
        return List.copyOf(storage.keySet());
    }

    @Override
    public String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null) {
            throw new NullPointerException("Имя файла не может быть null");
        }
        return UUID.randomUUID() + "_" + originalFileName;
    }

    public void clear() {
        storage.clear();
    }

    public int size() {
        return storage.size();
    }
}
