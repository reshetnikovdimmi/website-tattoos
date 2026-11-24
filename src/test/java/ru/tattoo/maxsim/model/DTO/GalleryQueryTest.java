package ru.tattoo.maxsim.model.DTO;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GalleryQueryTest {

    @Test
    void normalizesAllGalleryAndTrimsStyle() {
        assertThat(GalleryQuery.of(0, 9, null).category()).isNull();
        assertThat(GalleryQuery.of(0, 9, "  ").category()).isNull();
        assertThat(GalleryQuery.of(0, 9, "Вся галерея").category()).isNull();
        assertThat(GalleryQuery.of(0, 9, "  Реализм  ").category()).isEqualTo("Реализм");
    }

    @Test
    void keepsSelectedStyleForTemplate() {
        assertThat(GalleryQuery.of(0, 9, null).selectedStyle()).isEmpty();
        assertThat(GalleryQuery.of(0, 9, "Реализм").selectedStyle()).isEqualTo("Реализм");
        assertThat(GalleryQuery.of(0, 9, "Вся галерея").selectedStyle()).isEmpty();
    }

    @Test
    void normalizesPageSizeAndStyleInConstructor() {
        GalleryQuery query = GalleryQuery.of(-3, 7, "  Реализм  ");

        assertThat(query.page()).isZero();
        assertThat(query.size()).isEqualTo(9);
        assertThat(query.style()).isEqualTo("Реализм");
        assertThat(query.category()).isEqualTo("Реализм");
    }
}
