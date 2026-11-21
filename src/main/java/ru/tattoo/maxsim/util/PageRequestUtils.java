package ru.tattoo.maxsim.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Единые правила пагинации для галереи и эскизов.
 */
public final class PageRequestUtils {

    private PageRequestUtils() {
    }

    public static PageRequest of(int page, int size) {
        return PageRequest.of(Math.max(page, 0), PageSize.normalizeSize(size),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    public static PageRequest last(Page<?> page) {
        int lastPage = Math.max(page.getTotalPages() - 1, 0);
        return PageRequest.of(lastPage, page.getSize(), Sort.by(Sort.Direction.DESC, "id"));
    }
}
