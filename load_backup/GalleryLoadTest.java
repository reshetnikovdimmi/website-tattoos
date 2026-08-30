package ru.tattoo.maxsim.load_backup;

import io.gatling.javaapi.core.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * 7.2. Тестирование загрузки галереи и работы с изображениями
 */
public class GalleryLoadTest extends LoadTestBase {

    private static final Logger log = LoggerFactory.getLogger(GalleryLoadTest.class);

    private static final int TOTAL_IMAGES = 100;
    private static final int CONCURRENT_USERS = 30;

    @Test
    public void testGalleryLoadConcurrent() {
        log.info("🚀 Запуск теста одновременной загрузки галереи");

        ScenarioBuilder scenario = scenario("Загрузка галереи")
                .exec(http("GET /gallery")
                        .get("/gallery")
                        .check(status().is(200))
                        .check(responseTimeInMillis().lt(3000)))
                .pause(Duration.ofMillis(500))
                .exec(http("GET gallery with filter")
                        .get("/gallery/filter/style/Вся галерея")
                        .check(status().is(200)))
                .pause(Duration.ofMillis(300));

        PopulationBuilder population = scenario.injectOpen(
                rampUsers(20).during(Duration.ofSeconds(30)),
                constantUsersPerSec(30).during(Duration.ofSeconds(90))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест галереи завершен");
    }

    @Test
    public void testGalleryImageLoading() {
        log.info("🚀 Запуск теста загрузки изображений галереи");

        // Сценарий загрузки изображений с пагинацией
        ScenarioBuilder scenario = scenario("Пагинация галереи")
                .exec(http("GET /gallery/page/0/9")
                        .get("/gallery/0/9")
                        .check(status().is(200)))
                .pause(Duration.ofMillis(300))
                .exec(http("GET /gallery/page/1/9")
                        .get("/gallery/1/9")
                        .check(status().is(200)))
                .pause(Duration.ofMillis(300))
                .exec(http("GET /gallery/page/2/9")
                        .get("/gallery/2/9")
                        .check(status().is(200)));

        PopulationBuilder population = scenario.injectOpen(
                constantUsersPerSec(20).during(Duration.ofSeconds(60))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест пагинации галереи завершен");
    }

    @Test
    public void testGalleryWithBestImages() {
        log.info("🚀 Запуск теста фильтрации лучших изображений");

        ScenarioBuilder scenario = scenario("Лучшие изображения")
                .exec(http("GET /gallery?filter=best")
                        .get("/gallery?filter=best")
                        .check(status().is(200)));

        PopulationBuilder population = scenario.injectOpen(
                rampUsers(25).during(Duration.ofSeconds(30)),
                constantUsersPerSec(25).during(Duration.ofSeconds(60))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест фильтрации завершен");
    }
}