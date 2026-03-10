package ru.tattoo.maxsim.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

public class ImageUtilsTest {

    @TempDir
    Path tempDir;

    private String originalUploadPath;
    private static  final String TEST_DIR = "test-images";

    @BeforeEach
    void setup() throws IOException{
        // Сохраняем оригинальный путь и подменяем на тестовый
     //   originalUploadPath = ImageUtils.UPLOAD_DIRECTORY;
    }
}
