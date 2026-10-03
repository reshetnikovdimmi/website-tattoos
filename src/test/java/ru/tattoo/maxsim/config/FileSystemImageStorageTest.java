package ru.tattoo.maxsim.storage.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import ru.tattoo.maxsim.storage.Impl.FileSystemImageStorage;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FileSystemImageStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveFileIntoConfiguredDirectory() throws Exception {
        FileSystemImageStorage storage = new FileSystemImageStorage(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile(
            "file", "photo.png", "image/png", "bytes".getBytes());
        String savedName = storage.saveImage(file, "photo.png");
        assertThat(savedName).isEqualTo("photo.png");
        Path saved = tempDir.resolve("photo.png");
        assertThat(Files.exists(saved)).isTrue();
        assertThat(Files.readAllBytes(saved)).isEqualTo("bytes".getBytes());
    }

    @Test
    void shouldCreateDirectoryIfNotExists() {
        Path nonExistent = tempDir.resolve("nested/dir");
        assertThat(Files.exists(nonExistent)).isFalse();

        new FileSystemImageStorage(nonExistent.toString());

        assertThat(Files.exists(nonExistent)).isTrue();
    }
}
