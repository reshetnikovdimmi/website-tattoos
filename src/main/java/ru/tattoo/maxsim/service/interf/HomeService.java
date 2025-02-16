package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Home;
import java.io.IOException;

public interface HomeService  extends CRUDService<Home, Long> {

    void saveImg(MultipartFile fileImport, String category) throws IOException;

    void deleteImg(Long id) throws IOException;

}
