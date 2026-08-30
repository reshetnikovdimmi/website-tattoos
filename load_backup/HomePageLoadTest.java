package ru.tattoo.maxsim.load_backup;

import io.gatling.javaapi.core.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * 7.1. Тестирование загрузки главной страницы (1000 запросов)
 */
public class HomePageLoadTest extends LoadTestBase {

    private static final Logger log = LoggerFactory.getLogger(HomePageLoadTest.class);

    private static final int TOTAL_REQUESTS = 1000;
    private static final int CONCURRENT_USERS = 50;
    private static final int RAMP_UP_SECONDS = 30;
    private static final int TEST_DURATION_SECONDS = 120;

    @Test
    public void testHomePageLoad1000Requests() {
        log.info("🚀 Запуск нагрузочного теста: 1000 запросов главной страницы");

        // Сценарий с многократными запросами
        ScenarioBuilder scenario = scenario("1000 запросов главной страницы")
                .repeat(TOTAL_REQUESTS / CONCURRENT_USERS).on(
                        exec(http("GET /")
                                .get("/")
                                .check(status().is(200))
                                .check(responseTimeInMillis().lte(2000)))
                                .pause(Duration.ofMillis(100))
                );

        // Настройка нагрузки
        PopulationBuilder population = scenario.injectOpen(
                rampUsers(CONCURRENT_USERS).during(Duration.ofSeconds(RAMP_UP_SECONDS)),
                constantUsersPerSec(CONCURRENT_USERS).during(Duration.ofSeconds(TEST_DURATION_SECONDS))
        );

        // Запуск теста
        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест завершен");
    }

    @Test
    public void testHomePageConcurrentLoad() {
        log.info("🚀 Запуск теста одновременной нагрузки: 50 пользователей");

        ScenarioBuilder scenario = scenario("Одновременная загрузка")
                .exec(http("GET / - Home")
                        .get("/")
                        .check(status().is(200))
                        .check(responseTimeInMillis().lt(1500)))
                .exec(http("GET /gallery")
                        .get("/gallery")
                        .check(status().is(200))
                        .check(responseTimeInMillis().lt(2000)))
                .exec(http("GET /sketches")
                        .get("/sketches")
                        .check(status().is(200))
                        .check(responseTimeInMillis().lt(2000)));

        PopulationBuilder population = scenario.injectOpen(
                rampUsers(50).during(Duration.ofSeconds(30)),
                constantUsersPerSec(50).during(Duration.ofSeconds(60))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест одновременной нагрузки завершен");
    }

    @Test
    public void testHomePageStressLoad() {
        log.info("🚀 Запуск стресс-теста главной страницы");

        // Постепенное увеличение нагрузки до предела
        ScenarioBuilder scenario = scenario("Стресс-тест")
                .exec(http("GET /")
                        .get("/")
                        .check(status().is(200)));

        PopulationBuilder population = scenario.injectOpen(
                rampUsers(10).during(Duration.ofSeconds(30)),
                rampUsers(50).during(Duration.ofSeconds(30)),
                rampUsers(100).during(Duration.ofSeconds(30)),
                rampUsers(200).during(Duration.ofSeconds(30)),
                rampUsers(0).during(Duration.ofSeconds(30))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Стресс-тест завершен");
    }
}