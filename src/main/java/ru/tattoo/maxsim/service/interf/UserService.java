package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.User;

import java.io.IOException;
import java.security.Principal;


public interface UserService extends CRUDService<User, Long> {
    void updateUserAvatar(MultipartFile fileImport, Principal principal) throws IOException;
    UserDTO findByLogin(String login);
}
