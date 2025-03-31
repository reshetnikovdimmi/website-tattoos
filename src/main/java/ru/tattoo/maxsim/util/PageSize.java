package ru.tattoo.maxsim.util;


import lombok.Getter;

import java.util.ArrayList;
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
}