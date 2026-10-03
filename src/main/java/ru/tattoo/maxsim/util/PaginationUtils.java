package ru.tattoo.maxsim.util;

import org.apache.commons.collections4.ListUtils;

import java.util.Collections;
import java.util.List;

/**
 * Утилита для пагинации списков.
 * Вынесена из ImageUtils — пагинация не относится к хранилищу изображений.
 */
public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null || list.isEmpty() || size <= 0) {
            return Collections.emptyList();
        }
        return ListUtils.partition(list, size);
    }
}
