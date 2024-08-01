package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;

import java.io.IOException;

public interface ImagesService extends CRUDService<Images, Long> {
    void saveImg(MultipartFile fileImport, String description, String category) throws IOException;

    void deleteImg(Long id) throws IOException;
}
