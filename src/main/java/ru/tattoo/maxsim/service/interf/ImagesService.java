package ru.tattoo.maxsim.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tattoo.maxsim.model.DTO.GalleryQuery;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;

import java.security.Principal;

public interface ImagesService extends CRUDService<Images, Long> {
    Page<Images> getPagedImages(Pageable p);

    Page<Images> findByCategory(String style, Pageable p);

    Page<Images> getPagedImages(String userName, Pageable p);

    PageResult<Images> getGalleryPage(GalleryQuery query, Principal principal);

    Iterable<Images> findByFlagTrue();

    void updateImageFlag(Long id, boolean flag);
}
