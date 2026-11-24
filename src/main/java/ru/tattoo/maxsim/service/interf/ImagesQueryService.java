package ru.tattoo.maxsim.service.interf;

import ru.tattoo.maxsim.model.DTO.GalleryQuery;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;

import java.util.List;

/**
 * Чтение галереи: страницы, фильтр, избранные изображения.
 */
public interface ImagesQueryService {

    PageResult<Images> getGalleryPage(GalleryQuery query, String username);

    List<Images> findByFlagTrue();

    List<Images> findAll();
}
