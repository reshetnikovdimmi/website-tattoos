package ru.tattoo.maxsim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrphanImageCleanupServiceTest {

    private ImageStorage imageStorage;
    private ImageReferenceService referenceService;
    private OrphanImageCleanupService cleanupService;

    @BeforeEach
    void setUp() {
        imageStorage = mock(ImageStorage.class);
        referenceService = mock(ImageReferenceService.class);
        cleanupService = new OrphanImageCleanupService(
                imageStorage, referenceService, Duration.ofHours(24));
    }

    @Test
    void deletesOnlyOldUnreferencedFiles() throws Exception {
        when(imageStorage.listImages()).thenReturn(List.of(
                "old-orphan.jpg", "referenced.jpg", "young.jpg"));
        when(referenceService.collectReferencedImageNames()).thenReturn(Set.of("referenced.jpg"));
        when(imageStorage.lastModified("old-orphan.jpg"))
                .thenReturn(Instant.now().minus(Duration.ofHours(48)));
        when(imageStorage.lastModified("young.jpg")).thenReturn(Instant.now());

        OrphanImageCleanupService.CleanupResult result =
                cleanupService.cleanupOrphans(Duration.ofHours(24));

        verify(imageStorage).deleteImage("old-orphan.jpg");
        verify(imageStorage, never()).deleteImage("referenced.jpg");
        verify(imageStorage, never()).deleteImage("young.jpg");
        assertThat(result.scanned()).isEqualTo(3);
        assertThat(result.deleted()).isEqualTo(1);
        assertThat(result.retained()).isEqualTo(2);
    }

    @Test
    void doesNotDeleteWhenReferenceLookupFails() throws Exception {
        when(imageStorage.listImages()).thenReturn(List.of("orphan.jpg"));
        when(referenceService.collectReferencedImageNames())
                .thenThrow(new IllegalStateException("db unavailable"));

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> cleanupService.cleanupOrphans(Duration.ZERO))
                .isInstanceOf(IllegalStateException.class);

        verify(imageStorage, never()).deleteImage("orphan.jpg");
    }
}
