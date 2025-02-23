package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.User;

import java.io.IOException;


public interface UserService extends CRUDService<User, Long> {
    void saveImg(MultipartFile fileImport, Long id) throws IOException;
}
