package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.InterestingWorks;

import java.io.IOException;

public interface InterestingWorksService extends CRUDService<InterestingWorks, Long>{
    void saveImg(MultipartFile fileImport, String description) throws IOException;

    void deleteImg(Long id) throws IOException;
}
