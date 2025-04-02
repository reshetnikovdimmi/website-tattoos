package ru.tattoo.maxsim.service.impl;

import io.micrometer.common.util.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.exceptions.FileDeletionException;
import ru.tattoo.maxsim.exceptions.UserNotFoundException;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends AbstractCRUDService<User, Long> implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    CrudRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    public List<User> findAll() {
        return (List<User>) getRepository().findAll();
    }


    @Override
    public void updateUserAvatar(MultipartFile fileImport, Principal principal) throws IOException {
        User user = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Пользователь " + principal.getName() + " не найден"));

        if (!StringUtils.isBlank(user.getAvatar()) && ImageUtils.existsImage(user.getAvatar())) {
            try {
                ImageUtils.deleteImage(user.getAvatar());
            } catch (IOException e) {
                throw new FileDeletionException("Ошибка удаления файла" +"-->"+ e);
            }
        }

        String uniqueFileName = ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename());
        ImageUtils.saveImage(fileImport, uniqueFileName);
        user.setAvatar(uniqueFileName);
        userRepository.save(user);
    }



    @Override
    public UserDTO findByLogin(String login) {
        return modelMapper.map(userRepository.findByLogin(login).orElse(null), UserDTO.class);
    }

}
