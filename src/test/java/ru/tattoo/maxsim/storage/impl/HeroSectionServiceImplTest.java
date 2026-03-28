package ru.tattoo.maxsim.storage.impl;


import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.CrudRepository;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.repository.HomeHeroSectionRepository;
import ru.tattoo.maxsim.service.impl.HeroSectionServiceImpl;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.*;

@Epic("Сервисный слой")
@Feature(value = "HeroSectionServiceImpl")
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты HeroSectionServiceImpl")
public class HeroSectionServiceImplTest {
    @Mock
    private HomeHeroSectionRepository repository;

    @Mock
    private ImageStorage imageStorage;

    @Captor
    private ArgumentCaptor<HomeHeroSection> entityCaptor;

    private HeroSectionServiceImpl service;

    @BeforeEach
    @Step("Инициализация тестового сервиса")
    void setUp() throws Exception {
        service = new HeroSectionServiceImpl();

        // Внедряем зависимости через reflection (так как нет конструктора)
        java.lang.reflect.Field repositoryField =
                HeroSectionServiceImpl.class.getDeclaredField("homeHeroSectionRepository");
        repositoryField.setAccessible(true);
        repositoryField.set(service, repository);

        java.lang.reflect.Field imageStorageField =
                HeroSectionServiceImpl.class.getDeclaredField("imageStorage");
        imageStorageField.setAccessible(true);
        imageStorageField.set(service, imageStorage);

        Allure.addAttachment("Описание",
                this.getClass().getSimpleName() + " - Создан сервис с моками");
    }

    // =========================================================================
    // 1.2.4 Тестирование специфичных методов
    // =========================================================================

    @Nested
    @DisplayName("1.2.4 Специфичные методы HeroSectionServiceImpl")
    @Story("Специфичная логика HeroSection")
    class SpecificMethodsTests {
        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("getImageStorage - должен вернуть imageStorage")
        void getImageStorage_ShouldReturnImageStorage() {
            ImageStorage result = service.getImageStorage();
            assertNotNull(result);
            assertEquals(imageStorage, result);

            Allure.addAttachment("Результат", "ImageStorage успешно получен");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("getImageFileName - должен вернуть имя файла из сущности")
        @Description("Проверяет получение имени файла из сущности")
        void getImageFileName_ShouldReturnImageFileName() {
            HomeHeroSection entity = new HomeHeroSection();
            String expectedFileName = "test-image.jpg";
            entity.setImageName(expectedFileName);

            String result = service.getImageFileName(entity);

            assertEquals(expectedFileName, result);
            Allure.addAttachment("Результат", "Имя файла: " + result);
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("getImageFileName - должен вернуть null для null сущности")
        @Description("Проверяет безопасную обработку null")
        void getImageFileName_ShouldReturnNullForNullEntity() {
            String result = service.getImageFileName(null);
            assertNull(result);
            Allure.addAttachment("Результат", "Для null сущности вернулся null");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("setImageFileName - должен установить имя файла в сущность")
        @Description("Проверяет установку имени файла в сущность")
        void setImageFileName_ShouldSetImageFileName() {
            HomeHeroSection entity = new HomeHeroSection();
            String fileName = "new-image.jpg";

            service.setImageFileName(entity, fileName);

            assertEquals(fileName, entity.getImageName());
            Allure.addAttachment("Результат", "Имя файла установлено: " + fileName);
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("getRepository - должен вернуть HomeHeroSectionRepository")
        @Description("Проверяет, что getRepository возвращает правильный репозиторий")
        void getRepository_ShouldReturnHomeHeroSectionRepository() {
            CrudRepository<HomeHeroSection, Long> result = service.getRepository();

            assertNotNull(result);
            assertEquals(repository, result);

            Allure.addAttachment("Результат", "Репозиторий успешно получен");
        }
    }
    // =========================================================================
    // 1.2.7 Тестирование наследованных CRUD методов
    // =========================================================================

    @Nested
    @DisplayName("1.2.7 Наследованные CRUD операции")
    @Story("Базовые CRUD операции от AbstractCRUDService")
    class InheritedCrudTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("create - должен сохранить сущность")
        @Description("Проверяет базовую операцию создания сущности")
        void create_ShouldSaveEntity() {
            HomeHeroSection entity = new HomeHeroSection();
            entity.setSection("home");

            // Настраиваем мок - когда repository.save вызывается, возвращаем ту же сущность
            when(repository.save(any(HomeHeroSection.class))).thenReturn(entity);

            service.create(entity);

            verify(repository).save(entity);
            Allure.addAttachment("Результат", "Сущность сохранена в репозиторий");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("findById - должен вернуть сущность по ID")
        @Description("Проверяет поиск сущности по идентификатору")
        void findById_ShouldReturnEntity() {
            Long id = 1L;
            HomeHeroSection expected = new HomeHeroSection();
            expected.setId(id);
            when(repository.findById(id)).thenReturn(Optional.of(expected));

            HomeHeroSection result = service.findById(id);

            assertNotNull(result);
            assertEquals(id, result.getId());
            Allure.addAttachment("Результат", "Сущность найдена по ID: " + id);
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("update - должен обновить сущность")
        @Description("Проверяет обновление существующей сущности")
        void update_ShouldUpdateEntity() {
            HomeHeroSection entity = new HomeHeroSection();
            entity.setId(1L);
            when(repository.save(entity)).thenReturn(entity);

            HomeHeroSection result = service.update(entity);

            verify(repository).save(entity);
            assertNotNull(result);
            Allure.addAttachment("Результат", "Сущность обновлена");
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("deleteById - должен удалить сущность")
        @Description("Проверяет удаление сущности по идентификатору")
        void deleteById_ShouldDeleteEntity() throws IOException {
            Long id = 1L;

            service.deleteById(id);

            verify(repository).deleteById(id);
            Allure.addAttachment("Результат", "Сущность удалена по ID: " + id);
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @DisplayName("findAll - должен вернуть все сущности")
        @Description("Проверяет получение всех сущностей")
        void findAll_ShouldReturnAllEntities() {
            List<HomeHeroSection> entities = Arrays.asList(
                    new HomeHeroSection(), new HomeHeroSection()
            );
            when(repository.findAll()).thenReturn(entities);

            List<HomeHeroSection> result = service.findAll();

            assertEquals(2, result.size());
            Allure.addAttachment("Результат", "Найдено сущностей: " + result.size());
        }
    }
}
