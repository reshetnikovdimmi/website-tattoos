package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Home;
import java.io.IOException;
import java.util.List;

public interface HomeService  extends CRUDService<Home, Long> {

    void saveImg(MultipartFile fileImport, String category, String textH1, String textH2, String textH3) throws IOException;

    void deleteImg(Long id) throws IOException;

    List<Home> findByCategory(String s);
}
