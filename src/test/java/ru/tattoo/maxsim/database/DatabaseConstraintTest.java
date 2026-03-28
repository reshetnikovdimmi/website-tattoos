package ru.tattoo.maxsim.database;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.repository.HomeHeroSectionRepository;

import static org.junit.jupiter.api.Assertions.*;

@Epic("База данных")
@Feature("Constraints")
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Тесты ограничений базы данных")
class DatabaseConstraintsTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HomeHeroSectionRepository heroSectionRepository;

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("NOT NULL constraint")
    @DisplayName("2.3.2.1 - NOT NULL constraint - проверка обязательных полей")
    @Description("Проверяет, что NOT NULL constraints работают")
    void notNullConstraintShouldWork() {
        Allure.step("Создание сущности без обязательного поля section");

        HomeHeroSection section = new HomeHeroSection();
        // Не устанавливаем обязательное поле section

        Allure.addAttachment("Сущность", "section = null (обязательное поле)");

        Allure.step("Попытка сохранить сущность - ожидается исключение");

        assertThrows(DataIntegrityViolationException.class, () -> {
            heroSectionRepository.save(section);
        }, "Должно быть выброшено исключение при нарушении NOT NULL constraint");

        Allure.step("✅ NOT NULL constraint работает корректно");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("UNIQUE constraint")
    @DisplayName("2.3.2.2 - UNIQUE constraint - проверка уникальности")
    @Description("Проверяет, что UNIQUE constraints работают")
    @Transactional
    void uniqueConstraintShouldWork() {
        Allure.step("Создание первой записи");

        HomeHeroSection section1 = new HomeHeroSection();
        section1.setSection("home");
        section1.setImageName("unique_image.jpg");
        section1.setDisplayOrder(1);
        heroSectionRepository.save(section1);

        Allure.addAttachment("Первая запись", "image_name = unique_image.jpg");

        Allure.step("Создание второй записи с таким же уникальным значением");

        HomeHeroSection section2 = new HomeHeroSection();
        section2.setSection("home");
        section2.setImageName("unique_image.jpg");
        section2.setDisplayOrder(2);

        Allure.step("Попытка сохранить дубликат - ожидается исключение");

        assertThrows(DataIntegrityViolationException.class, () -> {
            heroSectionRepository.save(section2);
        }, "Должно быть выброшено исключение при нарушении UNIQUE constraint");

        Allure.step("✅ UNIQUE constraint работает корректно");
    }
}
