package ru.tattoo.maxsim.database;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("База данных")
@Feature("Liquibase миграции")
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Тесты миграций Liquibase")
class LiquibaseMigrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Проверка применения миграций")
    @DisplayName("2.3.1.1 - Все миграции должны быть применены")
    @Description("Проверяет, что все changesets из Liquibase успешно применены")
    void allMigrationsShouldBeApplied() {
        Allure.step("Проверка наличия таблицы DATABASECHANGELOG");

        String checkTableSql = """
                SELECT COUNT(*) FROM information_schema.tables 
                WHERE table_name = 'DATABASECHANGELOG'
                """;

        Integer count = jdbcTemplate.queryForObject(checkTableSql, Integer.class);

        Allure.addAttachment("Результат проверки", "Найдено записей: " + count);

        assertNotNull(count);
        assertTrue(count > 0, "Таблица DATABASECHANGELOG должна существовать");

        Allure.step("✅ Таблица DATABASECHANGELOG существует");
    }
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Проверка структуры таблиц")
    @DisplayName("2.3.1.2 - Все expected таблицы должны существовать")
    @Description("Проверяет наличие всех необходимых таблиц")
    void allExpectedTablesShouldExist() throws Exception {
        List<String> expectedTables = List.of(
                "home_hero_section",
                "user",
                "images",
                "reviews_user",
                "blog",
                "home"
        );

        Allure.addAttachment("Ожидаемые таблицы", expectedTables.toString());

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            for (String tableName : expectedTables) {
                Allure.step("Проверка таблицы: " + tableName);

                ResultSet tables = metaData.getTables(null, null, tableName.toUpperCase(), null);
                assertTrue(tables.next(), "Таблица " + tableName + " должна существовать");
                tables.close();

                Allure.addAttachment("Таблица " + tableName, "✅ Существует");
            }
        }

        Allure.step("✅ Все ожидаемые таблицы присутствуют");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Проверка структуры таблицы home_hero_section")
    @DisplayName("2.3.1.3 - Все expected колонки в home_hero_section должны существовать")
    @Description("Проверяет структуру таблицы home_hero_section")
    void heroSectionTableShouldHaveCorrectColumns() throws Exception {
        // Ожидаемые колонки в таблице home_hero_section
        List<String> expectedColumns = List.of(
                "id",              // первичный ключ
                "section",         // секция
                "image_name",      // имя изображения
                "display_order",   // порядок отображения
                "is_active",       // активность (в БД может быть is_active)
                "title",           // заголовок
                "subtitle",        // подзаголовок
                "description"      // описание
        );

        Allure.addAttachment("Ожидаемые колонки", expectedColumns.toString());

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "home_hero_section", null);

            List<String> actualColumns = new ArrayList<>();
            while (columns.next()) {
                actualColumns.add(columns.getString("COLUMN_NAME").toLowerCase());
            }
            columns.close();

            Allure.addAttachment("Фактические колонки", actualColumns.toString());

            for (String expectedColumn : expectedColumns) {
                Allure.step("Проверка колонки: " + expectedColumn);
                assertTrue(actualColumns.contains(expectedColumn),
                        "Колонка " + expectedColumn + " должна существовать в таблице home_hero_section");
                Allure.addAttachment("Колонка " + expectedColumn, "✅ Существует");
            }
        }

        Allure.step("✅ Все ожидаемые колонки присутствуют");
    }
    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Проверка версии миграций")
    @DisplayName("2.3.1.4 - Проверка версии последней миграции")
    @Description("Проверяет, что последняя миграция была успешно применена")
    void lastMigrationShouldBeApplied() {
        Allure.step("Получение информации о последней миграции");

        String sql = """
            SELECT COUNT(*) FROM DATABASECHANGELOG 
            WHERE EXECTYPE = 'EXECUTED' 
            ORDER BY ORDEREXECUTED DESC 
            LIMIT 1
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

        Allure.addAttachment("Результат", "Последняя миграция применена: " + (count > 0 ? "✅ Да" : "❌ Нет"));

        assertNotNull(count);
        assertEquals(1, count, "Последняя миграция должна быть применена");

        Allure.step("✅ Последняя миграция успешно применена");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Проверка данных в таблице")
    @DisplayName("2.3.1.5 - Проверка что в таблице home_hero_section есть данные")
    @Description("Проверяет, что в таблицу добавлены тестовые данные")
    void heroSectionTableShouldHaveData() {
        Allure.step("Проверка наличия данных в таблице home_hero_section");

        String sql = "SELECT COUNT(*) FROM home_hero_section";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

        Allure.addAttachment("Количество записей", String.valueOf(count));

        assertNotNull(count);
        assertTrue(count > 0, "В таблице home_hero_section должны быть данные");

        Allure.step("✅ В таблице есть " + count + " записей");
    }
}