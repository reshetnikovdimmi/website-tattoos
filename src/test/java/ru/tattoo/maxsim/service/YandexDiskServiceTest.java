package ru.tattoo.maxsim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.tattoo.maxsim.exceptions.YandexDiskException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Проверяет двухшаговую загрузку файла на Яндекс.Диск:
 * GET /resources/upload → PUT на выданный href.
 */
class YandexDiskServiceTest {

    private MockRestServiceServer server;
    private YandexDiskService service;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        service = new YandexDiskService(restTemplate);
    }

    @Test
    void uploadFileShouldRequestUploadUrlAndPutContent(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("db.sql.gz");
        Files.writeString(file, "dump-bytes");

        server.expect(requestTo(startsWith("https://cloud-api.yandex.net/v1/disk/resources/upload")))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "OAuth test-token"))
            .andExpect(queryParam("path", "/tattoo-backups/db.sql.gz"))
            .andExpect(queryParam("overwrite", "true"))
            .andRespond(withSuccess(
                "{\"href\":\"https://upload.example/put-target\",\"method\":\"PUT\",\"templated\":false}",
                MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://upload.example/put-target"))
            .andExpect(method(HttpMethod.PUT))
            .andExpect(content().bytes("dump-bytes".getBytes(StandardCharsets.UTF_8)))
            .andRespond(withSuccess());

        service.uploadFile("test-token", file, "/tattoo-backups/db.sql.gz");

        server.verify();
    }

    @Test
    void createFolderShouldIgnoreConflict() {
        server.expect(requestTo(startsWith("https://cloud-api.yandex.net/v1/disk/resources")))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThatCode(() -> service.createFolder("token", "/tattoo-backups"))
            .doesNotThrowAnyException();

        server.verify();
    }

    @Test
    void uploadFileShouldFailWhenLocalFileMissing(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("nope.sql.gz");

        assertThatThrownBy(() -> service.uploadFile("token", missing, "/tattoo-backups/nope.sql.gz"))
            .isInstanceOf(YandexDiskException.class)
            .hasMessageContaining("не найден");
    }
}
