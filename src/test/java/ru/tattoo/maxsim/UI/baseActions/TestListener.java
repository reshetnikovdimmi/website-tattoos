package ru.tattoo.maxsim.UI.baseActions;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.Optional;

public class TestListener implements TestWatcher {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void testSuccessful(ExtensionContext context) {
        log.info("✅ Тест успешно пройден: {}", context.getDisplayName());
        attachScreenshot("Скриншот после успешного теста");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log.error("❌ Тест провален: {}, Причина: {}", context.getDisplayName(), cause.getMessage());
        attachScreenshot("Скриншот после падения теста");
        attachErrorDetails(cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        log.warn("⚠️ Тест прерван: {}", context.getDisplayName());
        attachScreenshot("Скриншот после прерывания");
        attachErrorDetails(cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        log.info("⏸️ Тест отключен: {}", context.getDisplayName());
    }

    private void attachScreenshot(String name) {
        try {
            if (BaseSeleniumTest.driver != null) {
                byte[] screenshot = ((TakesScreenshot) BaseSeleniumTest.driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
                log.info("📸 Скриншот добавлен в отчет: {}", name);
            }
        } catch (Exception e) {
            log.warn("Не удалось сделать скриншот: {}", e.getMessage());
        }
    }

    private void attachErrorDetails(Throwable cause) {
        Allure.addAttachment("Ошибка", "text/plain", cause.getMessage(), "txt");
        Allure.addAttachment("Стек трейс", "text/plain", getStackTraceAsString(cause), "txt");
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}