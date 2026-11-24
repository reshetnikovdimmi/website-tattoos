package ru.tattoo.maxsim.util;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum PageSize {

    IMG_9(9),
    IMG_12(12),
    IMG_15(15);

    private final int pageSize;

    PageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    private static final List<Integer> PAGE_SIZES = Arrays.stream(values())
            .mapToInt(PageSize::getPageSize)
            .boxed()
            .collect(Collectors.toUnmodifiableList());

    /** Неизменяемый список допустимых размеров страницы. */
    public static List<Integer> getPageSizes() {
        return PAGE_SIZES;
    }

    /** Разрешённые размеры страницы; всё остальное сводим к 9. */
    public static int normalizeSize(int requested) {
        return PAGE_SIZES.contains(requested) ? requested : IMG_9.pageSize;
    }
}
