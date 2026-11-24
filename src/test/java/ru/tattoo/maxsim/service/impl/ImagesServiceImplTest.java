package ru.tattoo.maxsim.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImagesServiceImplTest {

    private ImagesRepository imagesRepository;
    private ImagesServiceImpl imagesService;

    @BeforeEach
    void setUp() {
        imagesRepository = mock(ImagesRepository.class);
        imagesService = new ImagesServiceImpl(mock(ImageStorage.class), imagesRepository);
    }

    @Test
    void updatesFlagOfExistingImage() {
        Images image = new Images();
        image.setId(10L);
        image.setFlag(false);
        when(imagesRepository.findById(10L)).thenReturn(Optional.of(image));

        imagesService.updateImageFlag(10L, true);

        assertThat(image.isFlag()).isTrue();
        verify(imagesRepository).save(image);
    }

    @Test
    void rejectsUnknownImage() {
        when(imagesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imagesService.updateImageFlag(99L, true))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
