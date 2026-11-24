package ru.tattoo.maxsim.service.interf;

import ru.tattoo.maxsim.model.DTO.GalleryQuery;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;

public interface ImagesService extends CRUDService<Images, Long> {
    PageResult<Images> getGalleryPage(GalleryQuery query, String username);

    Iterable<Images> findByFlagTrue();

    void updateImageFlag(Long id, boolean flag);
}
