package ru.tattoo.maxsim.repository;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.tattoo.maxsim.model.HomeHeroSection;

import java.util.Optional;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Тесты HomeHeroSectionRepository")
public class HomeHeroSectionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HomeHeroSectionRepository repository;

    @Nested
    @DisplayName("2.1.1 Поиск по ID")
    class FindByIdTests {

        @Test
        @DisplayName("findById - должен вернуть сущность по ID")
        void findById_ShouldReturnEntity() {
            // Arrange
            HomeHeroSection section = new HomeHeroSection();
            section.setSection("home");
            section.setImageName("test.jpg");
            section.setDisplayOrder(1);

            Long id = entityManager.persistAndGetId(section, Long.class);

            // Act
            Optional<HomeHeroSection> result = repository.findById(id);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("home", result.get().getSection());
            assertEquals("test.jpg", result.get().getImageName());
        }

        @Test
        @DisplayName("findById - должен вернуть пустой Optional для несуществующего ID")
        void findById_ShouldReturnEmptyForNonExistentId() {
            Optional<HomeHeroSection> result = repository.findById(999L);
            assertTrue(result.isEmpty());
        }
    }

}
