package ru.tattoo.maxsim.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageSizeTest {

    @Test
    void exposesImmutableWhitelist() {
        assertThat(PageSize.getPageSizes()).containsExactly(9, 12, 15);
        assertThatThrownBy(() -> PageSize.getPageSizes().add(18))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void normalizesUnknownSizeToNine() {
        assertThat(PageSize.normalizeSize(7)).isEqualTo(9);
        assertThat(PageSize.normalizeSize(12)).isEqualTo(12);
    }
}
