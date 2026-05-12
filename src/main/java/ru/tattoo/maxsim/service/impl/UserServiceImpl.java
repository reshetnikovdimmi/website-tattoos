package ru.tattoo.maxsim.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.exceptions.FileDeletionException;
import ru.tattoo.maxsim.exceptions.UserNotFoundException;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.storage.ImageStorage;
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
@Slf4j
public class UserServiceImpl extends AbstractCRUDService<User, Long> implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    void prepareObject(User entity, String fileName) {
        setImageFileName(entity, fileName);
    }

    @Override
    CrudRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected String getImageFileName(User entity) {
        return entity != null ? entity.getAvatar() : null; // Обратите внимание: avatarName
    }

    @Override
    protected void setImageFileName(User entity, String fileName) {
        if (entity != null) {
            entity.setAvatar(fileName);
        }
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

    @Override
    public void updateUserProfile(UserDTO userDTO, String currentLogin, String password, String confirmPassword) {
        log.info("Обновление профиля пользователя: {}", currentLogin);

        // Находим существующего пользователя
        User existingUser = userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь " + currentLogin + " не найден"));

        // Проверяем уникальность логина (если изменился)
        if (existingUser.getLogin().equals(userDTO.getLogin())) {
            log.info("Проверка уникальности логина: {}", userDTO.getLogin());

                throw new IllegalArgumentException("Пользователь с логином '" + userDTO.getLogin() + "' уже существует");

        }else{
            existingUser.setLogin(userDTO.getLogin());
        }

        // Проверяем уникальность email (если изменился)
        if (existingUser.getEmail().equals(userDTO.getEmail())) {
            log.info("Проверка уникальности email: {}", userDTO.getEmail());

                throw new IllegalArgumentException("Пользователь с email '" + userDTO.getEmail() + "' уже существует");

        }else{
            existingUser.setEmail(userDTO.getEmail());
        }

        // Обновляем пароль, если он был введен
        if (password != null && !password.trim().isEmpty()) {
            log.info("Обновление пароля");
            if (password.length() < 6) {
                throw new IllegalArgumentException("Пароль должен содержать минимум 6 символов");
            }
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Пароли не совпадают");
            }
            existingUser.setPassword(passwordEncoder.encode(password));
        }

        // Сохраняем изменения
        User savedUser = userRepository.save(existingUser);
        log.info("Профиль пользователя {} успешно обновлен", savedUser.getLogin());

        // Обновляем DTO для ответа
        modelMapper.map(savedUser, userDTO);
    }

}
