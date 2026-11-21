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

    private static volatile List<Integer> cachedSizes;

    public static synchronized List<Integer> getLisPageSize() {
        if (cachedSizes == null) {
            cachedSizes = Arrays.stream(values())
                    .mapToInt(PageSize::getPageSize)
                    .boxed()
                    .collect(Collectors.toList());
        }
        return cachedSizes;
    }

    /** Разрешённые размеры страницы; всё остальное сводим к 9. */
    public static int normalizeSize(int requested) {
        return Arrays.stream(values())
                .anyMatch(size -> size.pageSize == requested)
                ? requested
                : IMG_9.pageSize;
    }
}