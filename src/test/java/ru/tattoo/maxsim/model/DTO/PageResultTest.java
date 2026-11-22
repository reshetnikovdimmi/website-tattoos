package ru.tattoo.maxsim.model.DTO;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.tattoo.maxsim.model.Images;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResultTest {

    @Test
    void exposesFlatItemsAndPageMetadata() {
        Images image = new Images();
        image.setId(1L);
        image.setImageName("image.jpg");

        PageResult<Images> result = PageResult.of(
                new PageImpl<>(List.of(image), PageRequest.of(1, 9), 20));

        assertThat(result.getItems()).containsExactly(image);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(9);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(20);
        assertThat(result.getSelect()).containsExactly(9, 12, 15);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.isHasPrevious()).isTrue();
    }
}
