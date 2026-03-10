package ru.tattoo.maxsim.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import ru.tattoo.maxsim.storage.ImageStorage;
import ru.tattoo.maxsim.storage.Impl.FileSystemImageStorage;
import ru.tattoo.maxsim.storage.Impl.InMemoryImageStorage;

@Configuration
public class StorageConfig {
    /**
     * Продакшен реализация (файловая система)
     * Используется для профилей, кроме "test"
     */
    @Bean
    @Primary
    @Profile("!test")
    public ImageStorage fileSystemImageStorage(
            @Value("${upload.directory:uploads/images}") String uploadPath) {
        return new FileSystemImageStorage(uploadPath);
    }

    /**
     * Тестовая реализация (в памяти)
     * Используется только для профиля "test"
     */
    @Bean
    @Profile("test")
    public ImageStorage inMemoryImageStorage() {
        return new InMemoryImageStorage();
    }

    /**
     * Для локальной разработки (удобно)
     * Использует временную папку
     */
    @Bean
    @Profile("dev")
    public ImageStorage devFileSystemImageStorage() {
        String tempDir = System.getProperty("java.io.tmpdir") + "/tattoo-uploads";
        System.out.println("📁 Dev режим: файлы в " + tempDir);
        return new FileSystemImageStorage(tempDir);
    }
}
