package ru.tattoo.maxsim.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.GalleryDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.Sketches;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface ImagesService extends CRUDService<Images, Long> {

    void saveImg(MultipartFile fileImport, String description, String category, String userName) throws IOException;

    void deleteImg(Long id) throws IOException;

    Page<Images> partition(Pageable p);

    Page<Images> findByCategory(String style, Pageable p);

    List<List<Images>> pageList(Page<Images> images);

    boolean bestImage(Images images);

    Page<Images> partition(String userName, Pageable p);

    GalleryDTO pageList(Principal principal);
}
