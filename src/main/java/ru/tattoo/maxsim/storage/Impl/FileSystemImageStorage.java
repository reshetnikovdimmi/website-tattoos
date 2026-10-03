package ru.tattoo.maxsim.storage.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
public class FileSystemImageStorage implements ImageStorage {

    private final Path uploadDirectory;

    public FileSystemImageStorage(String uploadPath) {
        this.uploadDirectory = Paths.get(uploadPath).normalize().toAbsolutePath();
        initDirectory();
    }

    private void initDirectory() {
        try {
            Files.createDirectories(uploadDirectory);
            log.info("📁 Инициализирована директория загрузки: {}", uploadDirectory);
        } catch (IOException e) {
            log.error("❌ Не удалось создать директорию: {}", uploadDirectory, e);
            throw new RuntimeException("Не удалось создать директорию для загрузки файлов", e);
        }
    }

    @Override
    public String saveImage(MultipartFile file, String fileName) throws IOException {
        validateFile(file);
        Path filePath = resolveSafely(fileName);
        Files.write(filePath, file.getBytes());
        log.debug("✅ Файл сохранен: {}", fileName);
        return fileName;
    }

    @Override
    public void deleteImage(String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        boolean deleted = Files.deleteIfExists(resolveSafely(fileName));
        if (deleted) {
            log.debug("🗑️ Файл удален: {}", fileName);
        }
    }

    @Override
    public boolean existsImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        return Files.exists(resolveSafely(fileName));
    }

    @Override
    public byte[] getImage(String fileName) throws IOException {
        Path path = resolveSafely(fileName);
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + fileName);
        }
        return Files.readAllBytes(path);
    }

    @Override
    public String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null) {
            throw new NullPointerException("Имя файла не может быть null");
        }
        String cleanFileName = Paths.get(originalFileName).getFileName().toString();
        return UUID.randomUUID() + "_" + cleanFileName;
    }

    private void validateFile(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }
    }

    private Path resolveSafely(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName не может быть пустым");
        }
        Path resolved = uploadDirectory.resolve(fileName).normalize();
        if (!resolved.startsWith(uploadDirectory)) {
            throw new SecurityException("Path traversal: " + fileName);
        }
        return resolved;
    }
}
