package ru.tattoo.maxsim.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class PageRequestUtilsTest {

    @Test
    void clampsNegativePageAndUnknownSize() {
        var request = PageRequestUtils.of(-5, 7);

        assertThat(request.getPageNumber()).isZero();
        assertThat(request.getPageSize()).isEqualTo(9);
        assertThat(request.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Test
    void acceptsWhitelistedSize() {
        var request = PageRequestUtils.of(2, 12);

        assertThat(request.getPageNumber()).isEqualTo(2);
        assertThat(request.getPageSize()).isEqualTo(12);
    }
}
