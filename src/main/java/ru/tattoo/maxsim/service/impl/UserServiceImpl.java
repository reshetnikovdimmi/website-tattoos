package ru.tattoo.maxsim.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.exceptions.UserNotFoundException;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public void updateUserAvatar(MultipartFile fileImport, Long id) throws IOException {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));

        String uniqueFileName = ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename());
        user.setAvatar(uniqueFileName);

        ImageUtils.saveImage(fileImport, uniqueFileName);

        userRepository.save(user);
    }

    @Override
    public void deleteImg(Long id) throws IOException {
        Optional<String> imageName = userRepository.findNameById(id);
        imageName.ifPresent(name -> {
            try {
                ImageUtils.deleteImage(name);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка удаления файла", e);
            }
        });
        getRepository().deleteById(id);
    }

    @Override
    public UserDTO findByLogin(String login) {

        return modelMapper.map(userRepository.findByLogin(login).orElse(null), UserDTO.class);
    }

}
