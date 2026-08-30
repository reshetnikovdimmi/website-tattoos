package ru.tattoo.maxsim.load_backup;

import io.gatling.javaapi.core.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * 7.3. Тестирование работы с базой данных (1000 записей)
 */
public class DatabaseLoadTest extends LoadTestBase {

    private static final Logger log = LoggerFactory.getLogger(DatabaseLoadTest.class);
    private static final int TOTAL_RECORDS = 1000;

    @Test
    public void testDatabaseWrite1000Records() {
        log.info("🚀 Запуск теста записи 1000 записей в БД");

        // Сценарий регистрации пользователей
        ScenarioBuilder registerScenario = scenario("Регистрация пользователей")
                .feed(csv("users.csv").circular())
                .exec(http("POST /process-registration")
                        .post("/process-registration")
                        .formParam("login", "#{login}")
                        .formParam("email", "#{email}")
                        .formParam("password", "#{password}")
                        .formParam("confirmPassword", "#{password}")
                        .check(status().in(200, 302)));

        PopulationBuilder population = registerScenario.injectOpen(
                rampUsers(50).during(Duration.ofSeconds(60)),
                constantUsersPerSec(20).during(Duration.ofSeconds(120))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест записи завершен");
    }

    @Test
    public void testDatabaseRead1000Records() {
        log.info("🚀 Запуск теста чтения 1000 записей из БД");

        ScenarioBuilder scenario = scenario("Чтение данных")
                .exec(http("GET /gallery")
                        .get("/gallery")
                        .check(status().is(200)))
                .pause(Duration.ofMillis(200))
                .exec(http("GET /sketches")
                        .get("/sketches")
                        .check(status().is(200)))
                .pause(Duration.ofMillis(200))
                .exec(http("GET /reviews")
                        .get("/reviews")
                        .check(status().is(200)));

        PopulationBuilder population = scenario.injectOpen(
                constantUsersPerSec(50).during(Duration.ofSeconds(120))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест чтения завершен");
    }

    @Test
    public void testDatabaseMixedOperations() {
        log.info("🚀 Запуск теста смешанных операций с БД");

        FeederBuilder<String> feeder = csv("test_data.csv").circular();

        ScenarioBuilder scenario = scenario("Смешанные операции")
                .feed(feeder)
                .exec(
                        randomSwitch().on(
                                weight(30, exec(http("GET запрос")
                                        .get("/gallery")
                                        .check(status().is(200)))),
                                weight(20, exec(http("POST отзыв")
                                        .post("/reviews/user-import")
                                        .formParam("comment", "#{comment}")
                                        .formUpload("file", "src/test/resources/test-image.jpg")
                                        .check(status().is(200)))),
                                weight(30, exec(http("GET детали")
                                        .get("/gallery/#{id}")
                                        .check(status().is(200)))),
                                weight(20, exec(http("GET эскизы")
                                        .get("/sketches")
                                        .check(status().is(200))))
                        )
                );

        PopulationBuilder population = scenario.injectOpen(
                rampUsers(30).during(Duration.ofSeconds(60)),
                constantUsersPerSec(30).during(Duration.ofSeconds(180))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест смешанных операций завершен");
    }

    @Test
    public void testDatabaseConcurrentWrites() {
        log.info("🚀 Запуск теста конкурентной записи в БД");

        ScenarioBuilder scenario = scenario("Конкурентная запись")
                .exec(http("POST отзыв")
                        .post("/reviews/user-import")
                        .formParam("comment", "Тестовый отзыв ${random()}")
                        .formUpload("file", "src/test/resources/test-image.jpg")
                        .check(status().in(200, 302)));

        PopulationBuilder population = scenario.injectOpen(
                rampUsers(20).during(Duration.ofSeconds(30)),
                constantUsersPerSec(20).during(Duration.ofSeconds(90)),
                rampUsers(0).during(Duration.ofSeconds(30))
        );

        GatlingSimulation simulation = new GatlingSimulation(population);
        simulation.execute();

        log.info("✅ Тест конкурентной записи завершен");
    }
}