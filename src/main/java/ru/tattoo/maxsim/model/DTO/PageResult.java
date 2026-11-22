package ru.tattoo.maxsim.model.DTO;

import lombok.Getter;
import org.springframework.data.domain.Page;
import ru.tattoo.maxsim.util.PageSize;

import java.util.Collections;
import java.util.List;

/**
 * Единый результат постраничной выдачи для галереи и эскизов.
 */
@Getter
public class PageResult<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final int totalPages;
    private final long totalElements;
    private final List<Integer> select;

    public PageResult(List<T> items, int page, int size,
                      int totalPages, long totalElements, List<Integer> select) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.select = select;
    }

    public static <T> PageResult<T> of(Page<T> page) {
        List<T> items = page.hasContent() ? page.getContent() : Collections.emptyList();
        return new PageResult<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                PageSize.getLisPageSize()
        );
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public boolean isHasNext() {
        return page + 1 < totalPages;
    }

    public boolean isHasPrevious() {
        return page > 0;
    }
}
