package ru.tattoo.maxsim.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.tattoo.maxsim.model.DTO.GalleryQuery;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void anonymousCategoryUsesPublicQuery() {
        Page<Images> page = new PageImpl<>(List.of(), PageRequest.of(0, 9), 0);
        when(imagesRepository.findByCategory(eq("Реализм"), any(Pageable.class))).thenReturn(page);

        imagesService.getGalleryPage(GalleryQuery.of(0, 9, "Реализм"), null);

        verify(imagesRepository).findByCategory(eq("Реализм"), any(Pageable.class));
    }

    @Test
    void userCategoryUsesCategoryAndUserQuery() {
        Page<Images> page = new PageImpl<>(List.of(), PageRequest.of(0, 9), 0);
        when(imagesRepository.findByCategoryAndUser(eq("Реализм"), eq("alice"), any(Pageable.class)))
                .thenReturn(page);

        imagesService.getGalleryPage(GalleryQuery.of(0, 9, "Реализм"), "alice");

        verify(imagesRepository).findByCategoryAndUser(eq("Реализм"), eq("alice"), any(Pageable.class));
    }

    @Test
    void userWithoutCategoryUsesUserNameQuery() {
        Page<Images> page = new PageImpl<>(List.of(), PageRequest.of(0, 9), 0);
        when(imagesRepository.findByUserName(eq("alice"), any(Pageable.class))).thenReturn(page);

        imagesService.getGalleryPage(GalleryQuery.of(0, 9, null), "alice");

        verify(imagesRepository).findByUserName(eq("alice"), any(Pageable.class));
    }

    @Test
    void publicGalleryUsesFindAll() {
        Page<Images> page = new PageImpl<>(List.of(), PageRequest.of(0, 9), 0);
        when(imagesRepository.findAll(any(Pageable.class))).thenReturn(page);

        imagesService.getGalleryPage(GalleryQuery.of(0, 9, null), null);

        verify(imagesRepository).findAll(any(Pageable.class));
    }

    @Test
    void returnsFeaturedImagesAsList() {
        Images image = new Images();
        image.setId(7L);
        image.setFlag(true);
        when(imagesRepository.findByFlagTrue()).thenReturn(List.of(image));

        List<Images> result = imagesService.findByFlagTrue();

        assertThat(result).containsExactly(image);
        verify(imagesRepository).findByFlagTrue();
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
