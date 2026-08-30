package ru.tattoo.maxsim.load_backup;

import io.gatling.javaapi.core.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * 7.2. Тестирование одновременной загрузки изображений
 */
public class ImageUploadLoadTest extends LoadTestBase {

    private static final Logger log = LoggerFactory.getLogger(ImageUploadLoadTest.class);
    private static final String TEST_IMAGE_PATH = "src/test/resources/test-image.jpg";

    // Сессионные переменные для хранения кук авторизации
    private static final String SESSION_COOKIE = "JSESSIONID";

    @Test
    public void testConcurrentImageUpload() {
        log.info("🚀 Запуск теста одновременной загрузки изображений");

        // Сценарий авторизации
        ScenarioBuilder authScenario = scenario("Авторизация")
                .exec(http("POST /login")
                        .post("/login")
                        .formParam("username", "ADMIN")
                        .formParam("password", "admin")
                        .check(status().is(302))
                        .check(header("Set-Cookie").saveAs(SESSION_COOKIE)))
                .exec(session -> session.set("imageId", UUID.randomUUID().toString()));

        // Сценарий загрузки изображения
        ScenarioBuilder uploadScenario = scenario("Загрузка изображений")
                .feed(csv("image_data.csv").circular())
                .exec(http("POST /gallery/image-import")
                        .post("/gallery/image-import")
                        .header("Cookie", "#{" + SESSION_COOKIE + "}")
                        .formParam("description", "#{description}")
                        .formUpload("file", TEST_IMAGE_PATH)
                        .check(status().is(200)));

        PopulationBuilder population = authScenario
                .andThen(uploadScenario)
                .injectOpen(
                        rampUsers(10).during(Duration.ofSeconds(30)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(60))
                );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест загрузки изображений завершен");
    }

    @Test
    public void testBulkImageUpload() {
        log.info("🚀 Запуск теста пакетной загрузки изображений (100 изображений)");

        ScenarioBuilder scenario = scenario("Пакетная загрузка")
                .repeat(10).on(
                        exec(http("POST /gallery/image-import")
                                .post("/gallery/image-import")
                                .formParam("description", "Тестовое изображение ${random()}")
                                .formUpload("file", TEST_IMAGE_PATH)
                                .check(status().is(200)))
                                .pause(Duration.ofMillis(500))
                );

        PopulationBuilder population = scenario.injectOpen(
                rampUsers(10).during(Duration.ofSeconds(60))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест пакетной загрузки завершен");
    }

    @Test
    public void testImageUploadStress() {
        log.info("🚀 Запуск стресс-теста загрузки изображений");

        ScenarioBuilder scenario = scenario("Стресс-загрузка")
                .exec(http("POST /gallery/image-import")
                        .post("/gallery/image-import")
                        .formParam("description", "Стресс-тест ${random()}")
                        .formUpload("file", TEST_IMAGE_PATH)
                        .check(status().in(200, 500)));

        PopulationBuilder population = scenario.injectOpen(
                rampUsers(50).during(Duration.ofSeconds(30)),
                constantUsersPerSec(50).during(Duration.ofSeconds(120)),
                rampUsers(0).during(Duration.ofSeconds(30))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Стресс-тест загрузки завершен");
    }
}