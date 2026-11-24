package ru.tattoo.maxsim.service.interf;

import ru.tattoo.maxsim.model.DTO.GalleryQuery;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;

import java.util.List;

public interface ImagesService extends CRUDService<Images, Long> {
    PageResult<Images> getGalleryPage(GalleryQuery query, String username);

    List<Images> findByFlagTrue();

    void updateImageFlag(Long id, boolean flag);
}
