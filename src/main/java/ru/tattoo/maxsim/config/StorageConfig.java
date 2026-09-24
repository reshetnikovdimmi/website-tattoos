package ru.tattoo.maxsim.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import ru.tattoo.maxsim.storage.ImageStorage;
import ru.tattoo.maxsim.storage.Impl.FileSystemImageStorage;
import ru.tattoo.maxsim.storage.Impl.InMemoryImageStorage;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {

    private final StorageProperties props;

    /**
     * Основная реализация — файловая система.
     * Путь берётся из app.storage.upload-dir (ENV: APP_STORAGE_UPLOAD_DIR).
     */
    @Bean
    @Primary
    @Profile("!test")
    public ImageStorage fileSystemImageStorage() {
        log.info("Image storage: filesystem, uploadDir={}", props.getUploadDir());
        return new FileSystemImageStorage(props.getUploadDir());
    }

    /**
     * Тестовая реализация — в памяти.
     */
    @Bean
    @Profile("test")
    public ImageStorage inMemoryImageStorage() {
        log.info("Image storage: in-memory (test profile)");
        return new InMemoryImageStorage();
    }
}
