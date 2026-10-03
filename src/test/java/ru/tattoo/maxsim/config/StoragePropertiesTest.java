package ru.tattoo.maxsim.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class StoragePropertiesTest {

    @Autowired
    private StorageProperties props;

    @Test
    void shouldBindDefaults() {
        assertThat(props.getUrlPrefix()).isEqualTo("/images");
        assertThat(props.getCacheSeconds()).isEqualTo(3600);
        assertThat(props.getMaxFileSize()).isEqualTo(10_485_760L);
        assertThat(props.getUploadDir()).isNotBlank();
    }
}
