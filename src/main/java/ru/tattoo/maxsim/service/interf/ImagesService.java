package ru.tattoo.maxsim.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;

import java.io.IOException;
import java.util.List;

public interface ImagesService extends CRUDService<Images, Long> {
    void saveImg(MultipartFile fileImport, String description, String category) throws IOException;

    void deleteImg(Long id) throws IOException;

    Page<Images> partition(Pageable p);

    Page<Images> findByCategory(String style, Pageable p);

    Object pageList(Page<Images> images);
}
