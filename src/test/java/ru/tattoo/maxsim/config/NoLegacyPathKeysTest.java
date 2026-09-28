package ru.tattoo.maxsim.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NoLegacyPathKeysTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void shouldResolveNewKey() {
        assertThat(ctx.getEnvironment().getProperty("app.storage.upload-dir"))
            .as("Новый ключ должен быть задан")
            .isNotBlank();
    }

    @Test
    void shouldNotResolveOldKey() {
        assertThat(ctx.getEnvironment().getProperty("upload.directory"))
            .as("Старый ключ upload.directory не должен использоваться")
            .isNull();
    }
}
