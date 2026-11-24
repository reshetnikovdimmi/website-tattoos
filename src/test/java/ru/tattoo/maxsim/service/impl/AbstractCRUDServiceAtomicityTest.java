package ru.tattoo.maxsim.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.exceptions.FileUploadException;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractCRUDServiceAtomicityTest {

    private CrudRepository<TestEntity, Long> repository;
    private ImageStorage imageStorage;
    private TestService service;

    @BeforeEach
    void setUp() {
        repository = mock(CrudRepository.class);
        imageStorage = mock(ImageStorage.class);
        service = new TestService(repository, imageStorage);
    }

    @Test
    void deletesNewFileWhenDatabaseSaveFails() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "bytes".getBytes());
        when(imageStorage.generateUniqueFileName("photo.jpg")).thenReturn("saved.jpg");
        when(imageStorage.saveImage(file, "saved.jpg")).thenReturn("saved.jpg");
        when(repository.save(any(TestEntity.class))).thenThrow(new RuntimeException("db down"));

        assertThatThrownBy(() -> service.saveImg(file, new TestEntity()))
                .isInstanceOf(FileUploadException.class);

        verify(imageStorage).deleteImage("saved.jpg");
    }

    @Test
    void deletesDatabaseRowBeforeFile() throws Exception {
        TestEntity entity = new TestEntity(1L, "image.jpg");
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.deleteById(1L);

        var order = inOrder(repository, imageStorage);
        order.verify(repository).delete(entity);
        order.verify(imageStorage).deleteImage("image.jpg");
    }

    @Test
    void fileDeletionFailureDoesNotUndoDatabaseDelete() throws Exception {
        TestEntity entity = new TestEntity(1L, "image.jpg");
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        org.mockito.Mockito.doThrow(new IOException("locked"))
                .when(imageStorage).deleteImage("image.jpg");

        service.deleteById(1L);

        verify(repository).delete(entity);
    }

    @Test
    void findAllWorksWithIterableRepositoryResult() {
        TestEntity first = new TestEntity(1L, "one.jpg");
        TestEntity second = new TestEntity(2L, "two.jpg");
        when(repository.findAll()).thenReturn(List.of(first, second));

        List<TestEntity> result = service.findAll();

        assertThat(result).containsExactly(second, first);
    }

    private static final class TestEntity {
        private final Long id;
        private final String imageName;

        private TestEntity() {
            this(null, null);
        }

        private TestEntity(Long id, String imageName) {
            this.id = id;
            this.imageName = imageName;
        }
    }

    private static final class TestService extends AbstractCRUDService<TestEntity, Long> {
        private final CrudRepository<TestEntity, Long> repository;
        private final ImageStorage imageStorage;

        private TestService(CrudRepository<TestEntity, Long> repository, ImageStorage imageStorage) {
            this.repository = repository;
            this.imageStorage = imageStorage;
        }

        @Override
        protected ImageStorage getImageStorage() {
            return imageStorage;
        }

        @Override
        void prepareObject(TestEntity entity, String fileName) {
        }

        @Override
        CrudRepository<TestEntity, Long> getRepository() {
            return repository;
        }

        @Override
        protected String getImageFileName(TestEntity entity) {
            return entity.imageName;
        }

        @Override
        protected void setImageFileName(TestEntity entity, String fileName) {
        }
    }
}
