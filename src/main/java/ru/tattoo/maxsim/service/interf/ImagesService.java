package ru.tattoo.maxsim.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.GalleryDTO;
import ru.tattoo.maxsim.model.Images;

import java.io.IOException;
import java.security.Principal;

public interface ImagesService extends CRUDService<Images, Long> {

    void saveImg(MultipartFile fileImport, String description, String category, String userName) throws IOException;

    void deleteImg(Long id) throws IOException;

    Page<Images> getPagedImages(Pageable p);

    Page<Images> findByCategory(String style, Pageable p);

    boolean bestImage(Images images);

    Page<Images> getPagedImages(String userName, Pageable p);

    GalleryDTO getGalleryDto(String category, Principal principal, int pageSize, int pageNumber);

    Iterable<Images> findByFlagTrue();
}
