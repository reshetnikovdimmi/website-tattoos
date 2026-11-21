package ru.tattoo.maxsim.model.DTO;

import lombok.Getter;
import org.springframework.data.domain.Page;
import ru.tattoo.maxsim.util.PageSize;
import ru.tattoo.maxsim.util.PaginationUtils;

import java.util.Collections;
import java.util.List;

/**
 * Единый результат постраничной выдачи для галереи и эскизов.
 *
 * <p>Новые поля: {@code items}, {@code page}, {@code size}, {@code totalPages},
 * {@code totalElements}. Старые геттеры оставлены как deprecated-алиасы на время
 * миграции шаблонов ({@code galleryImg}, {@code sketchesImg}, {@code pageSize},
 * {@code number}, {@code currentPage}, {@code imagesTotal}).
 */
@Getter
public class PageResult<T> {

    private final List<T> items;
    private final List<List<T>> rows;
    private final int page;
    private final int size;
    private final int totalPages;
    private final long totalElements;
    private final List<Integer> select;

    public PageResult(List<T> items, List<List<T>> rows, int page, int size,
                      int totalPages, long totalElements, List<Integer> select) {
        this.items = items;
        this.rows = rows;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.select = select;
    }

    public static <T> PageResult<T> of(Page<T> page, int partitionSize) {
        List<T> items = page.hasContent() ? page.getContent() : Collections.emptyList();
        return new PageResult<>(
                items,
                PaginationUtils.partition(items, partitionSize),
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

    @Deprecated
    public List<List<T>> getGalleryImg() {
        return rows;
    }

    @Deprecated
    public List<List<T>> getSketchesImg() {
        return rows;
    }

    @Deprecated
    public int getCurrentPage() {
        return page;
    }

    @Deprecated
    public int getNumber() {
        return size;
    }

    @Deprecated
    public int getPageSize() {
        return totalPages;
    }

    @Deprecated
    public long getImagesTotal() {
        return totalElements;
    }
}
