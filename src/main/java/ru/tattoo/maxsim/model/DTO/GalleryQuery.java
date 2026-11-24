package ru.tattoo.maxsim.model.DTO;

import ru.tattoo.maxsim.util.PageSize;

/**
 * Нормализованный запрос галереи: страница, размер и выбранный стиль.
 * Стиль «Вся галерея», пустая строка и null означают отсутствие фильтра.
 */
public record GalleryQuery(int page, int size, String style) {

    public static final String ALL_GALLERY = "Вся галерея";
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 9;

    public GalleryQuery {
        page = Math.max(page, 0);
        size = PageSize.normalizeSize(size);
        style = normalizeStyle(style);
        if (ALL_GALLERY.equals(style)) {
            style = null;
        }
    }

    public static GalleryQuery of(int page, int size, String style) {
        return new GalleryQuery(page, size, style);
    }

    /** Категория для репозитория; null — все изображения. */
    public String category() {
        return style;
    }

    /** Значение для подсветки фильтра в шаблоне. */
    public String selectedStyle() {
        return style == null ? "" : style;
    }

    private static String normalizeStyle(String style) {
        if (style == null) {
            return null;
        }
        String trimmed = style.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
