package ru.tattoo.maxsim.model.DTO;

/**
 * Нормализованный запрос галереи: страница, размер и выбранный стиль.
 * Стиль «Вся галерея», пустая строка и null означают отсутствие фильтра.
 */
public record GalleryQuery(int page, int size, String style) {

    public static final String ALL_GALLERY = "Вся галерея";
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 9;

    public static GalleryQuery of(int page, int size, String style) {
        return new GalleryQuery(page, size, style);
    }

    /** Категория для репозитория; null — все изображения. */
    public String category() {
        if (style == null) {
            return null;
        }
        String normalized = style.trim();
        return normalized.isEmpty() || ALL_GALLERY.equals(normalized) ? null : normalized;
    }

    /** Значение для подсветки фильтра в шаблоне. */
    public String selectedStyle() {
        return style == null ? "" : style;
    }
}
