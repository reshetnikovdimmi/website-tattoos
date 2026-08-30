package ru.tattoo.maxsim.load_backup;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для запуска Gatling симуляций в JUnit тестах
 */
public class GatlingSimulation extends Simulation {

    private static final Logger log = LoggerFactory.getLogger(GatlingSimulation.class);
    private static final List<PopulationBuilder> populations = new ArrayList<>();

    public GatlingSimulation(PopulationBuilder population) {
        populations.add(population);
        setUp(populations.toArray(new PopulationBuilder[0]));
    }

    public void execute() {
        try {
            GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                    .resourcesDirectory("src/test/resources")
                    .resultsDirectory("target/gatling-results")
                    .simulationClass(this.getClass().getName());

            Gatling.fromMap(props.build());
            log.info("✅ Gatling симуляция выполнена успешно");
        } catch (Exception e) {
            log.error("❌ Ошибка выполнения Gatling симуляции: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}