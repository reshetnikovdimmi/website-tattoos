package ru.tattoo.maxsim.load_backup;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Базовый класс для нагрузочного тестирования
 * Использует Gatling для генерации нагрузки
 */
public class LoadTestBase {

    private static final Logger log = LoggerFactory.getLogger(LoadTestBase.class);

    protected static final String BASE_URL = "http://localhost:8080";
    protected static final int DEFAULT_RAMP_USERS = 10;
    protected static final int DEFAULT_DURATION_MINUTES = 5;

    // Конфигурация протокола HTTP
    protected static final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .check(status().is(200))
            .disableWarmingUp();

    // Базовые сценарии
    protected static final ScenarioBuilder homePageScenario = scenario("Главная страница")
            .exec(http("GET /")
                    .get("/")
                    .check(status().is(200))
                    .check(responseTimeInMillis().lt(2000)))
            .pause(Duration.ofMillis(500));

    protected static final ScenarioBuilder galleryScenario = scenario("Галерея")
            .exec(http("GET /gallery")
                    .get("/gallery")
                    .check(status().is(200))
                    .check(responseTimeInMillis().lt(3000)))
            .pause(Duration.ofMillis(500));

    protected static final ScenarioBuilder sketchesScenario = scenario("Эскизы")
            .exec(http("GET /sketches")
                    .get("/sketches")
                    .check(status().is(200))
                    .check(responseTimeInMillis().lt(3000)))
            .pause(Duration.ofMillis(500));

    protected static final ScenarioBuilder reviewsScenario = scenario("Отзывы")
            .exec(http("GET /reviews")
                    .get("/reviews")
                    .check(status().is(200))
                    .check(responseTimeInMillis().lt(3000)))
            .pause(Duration.ofMillis(500));

    protected static final ScenarioBuilder contactScenario = scenario("Контакты")
            .exec(http("GET /contact")
                    .get("/contact")
                    .check(status().is(200))
                    .check(responseTimeInMillis().lt(2000)))
            .pause(Duration.ofMillis(500));

    /**
     * Сценарий смешанной нагрузки
     */
    protected static final ScenarioBuilder mixedLoadScenario = scenario("Смешанная нагрузка")
            .exec(
                    randomSwitch().on(
                            weight(40, homePageScenario),
                            weight(25, galleryScenario),
                            weight(15, sketchesScenario),
                            weight(10, reviewsScenario),
                            weight(10, contactScenario)
                    )
            );

    /**
     * Постепенное увеличение нагрузки
     * @param users количество пользователей
     * @param durationSeconds длительность в секундах
     */
    protected static PopulationBuilder rampUsers(int users, int durationSeconds) {
        return mixedLoadScenario.injectOpen(
                rampUsers(users).during(Duration.ofSeconds(durationSeconds))
        );
    }

    /**
     * Постоянная нагрузка
     * @param users количество пользователей
     * @param durationSeconds длительность в секундах
     */
    protected static PopulationBuilder constantUsers(int users, int durationSeconds) {
        return mixedLoadScenario.injectOpen(
                constantUsersPerSec(users).during(Duration.ofSeconds(durationSeconds))
        );
    }

    /**
     * Пиковая нагрузка
     * @param users количество пользователей
     * @param durationSeconds длительность в секундах
     */
    protected static PopulationBuilder peakUsers(int users, int durationSeconds) {
        return mixedLoadScenario.injectOpen(
                rampUsers(users).during(Duration.ofSeconds(30)),
                constantUsersPerSec(users).during(Duration.ofSeconds(durationSeconds - 60)),
                rampUsers(0).during(Duration.ofSeconds(30))
        );
    }

    /**
     * Генерация случайного числа в диапазоне
     */
    protected static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Генерация случайной строки
     */
    protected static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return sb.toString();
    }
}