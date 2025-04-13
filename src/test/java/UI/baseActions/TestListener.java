package UI.baseActions;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestListener implements TestWatcher {
    @Override
    public void testSuccessful(ExtensionContext context) {
        // Выполняется, когда тест прошел успешно
        System.out.println("Test successful: " + context.getDisplayName());

      /*  Allure.attachment("data.txt", "This is the file content.");
        try (InputStream is = Files.newInputStream(Paths.get("/path/img.png"))) {
            Allure.attachment("image.png", is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        // Выполняется, когда тест завершился неудачно (например, из-за ошибки или несоответствия утверждений)
        System.out.println("Test failed: " + context.getDisplayName() + ", Cause: " + cause.getMessage());
    }
}
