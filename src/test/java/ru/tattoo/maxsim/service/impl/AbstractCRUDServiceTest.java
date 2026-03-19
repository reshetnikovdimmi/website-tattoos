package ru.tattoo.maxsim.service.impl;

import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.CrudRepository;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

@Epic("Сервисный слой")
@Feature(value = "AbstractCRUDService")
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты AbstractCRUDService")
public class AbstractCRUDServiceTest {


    @Mock
    private CrudRepository<TestEntity, Long> repository;

    @Mock
    private ImageStorage imageStorage;

    @Captor
    private ArgumentCaptor<TestEntity> entityCaptor;

    @Captor
    private ArgumentCaptor<String> fileNameCaptor;

    private TestCRUDService service;

    @BeforeEach
    @Step
    void setUp(){
        service=new TestCRUDService(repository,imageStorage);
        Allure.addAttachment("Описание",this.getClass().getSimpleName()+" - Создан тестовый сервис с моками");
    }

    @Nested
    @DisplayName("1.2.1 CRUD операции")
    class CrudOperations{
        @Test
        @Story("Создание сущности")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("create - должен сохранить сущность")
        @Description ("Проверяет базовую операцию создания сущности")
        void create_ShouldSaveEntity(){

            // Arrange
            TestEntity entity = new TestEntity();
            entity.setName("Test Name");
            Allure.addAttachment("Входные данные", "Сущность: Test Name");

            // Act
            service.create(entity);

            // Assert
            verify(repository, times(1)).save(entity);
            Allure.addAttachment("Результат", "Сущность сохранена в репозиторий");
        }
        @Test
        @Story("Обновление сущностей")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("update - должно обновить сущьность")
        @Description("Проверка обновления существующей cущости")
        void update_ShouldUpdateEntity(){
            // Arrange
            TestEntity entity = new TestEntity(1L, "Update Name");
            when(repository.save(entity)).thenReturn(entity);
            Allure.addAttachment("Входные данные" , "Сущность с ID: 1, Name: Updated Name");
            // Act
            TestEntity result=service.update(entity);
            //Assert
            verify(repository, times(1)).save(entity);
            assertNotNull(result);
            Allure.addAttachment("Рузультат","Сущность обновлена");
        }
        @Test
        @Story("Поиск по ID")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("findById - должен вернуть сущность по ID")
        @Description("Проверяет поиск сущности по идентификатору")
        void findById_ShouldReturnEntity() {
            // Arrange
            Long id = 1L;
            TestEntity expectedEntity = new TestEntity(id, "Found Entity");
            when(repository.findById(id)).thenReturn(Optional.of(expectedEntity));
            Allure.addAttachment("Входные данные", "Поиск по ID: " + id);

            // Act
            TestEntity result = service.findById(id);

            // Assert
            verify(repository, times(1)).findById(id);
            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals("Found Entity", result.getName());
            Allure.addAttachment("Результат", "Сущность найдена: " + result.getName());
        }
        @Test
        @Story("Удаление по ID")
        @Severity(SeverityLevel.CRITICAL)
        @DisplayName("deleteById - должен удалить сущность по ID")
        @Description("Проверяет удаление сущности по идентификатору")
        void deleteById_ShouldDeleteEntityById() throws IOException {
            // Arrange
            Long id = 1L;
            Allure.addAttachment("Входные данные", "Удаление по ID: " + id);

            // Act
            service.deleteById(id);

            // Assert
            verify(repository, times(1)).deleteById(id);
            Allure.addAttachment("Результат", "Сущность удалена по ID");
        }
    }


    /**
     * Тестовая реализация AbstractCRUDService для тестирования
     */
    class TestCRUDService extends AbstractCRUDService<TestEntity, Long> {

        private final CrudRepository<TestEntity, Long> repository;
        private final ImageStorage imageStorage;

        private TestCRUDService(CrudRepository<TestEntity, Long> repository, ImageStorage imageStorage) {
            this.repository = repository;
            this.imageStorage = imageStorage;
        }


        @Override
        protected ImageStorage getImageStorage() {
            return imageStorage;
        }

        @Override
        void prepareObject(TestEntity entity, String fileName) {
            setImageFileName(entity, fileName);
            entity.setSection("home");
        }

        @Override
        CrudRepository<TestEntity, Long> getRepository() {
            return repository;
        }

        @Override
        protected String getImageFileName(TestEntity entity) {
            return entity != null ? entity.getImageName() : null;
        }

        @Override
        protected void setImageFileName(TestEntity entity, String fileName) {
            if (entity != null) {
                entity.setImageName(fileName);
            }
        }
    }

    /**
     * Тестовая сущность
     */
    private class TestEntity {
        private Long id;
        private String name;
        private String imageName;
        private String section;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public TestEntity() {

        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }
    }


}
