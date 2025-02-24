package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.interf.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends AbstractCRUDService<User, Long> implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    CrudRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public void saveImg(MultipartFile fileImport, Long id) throws IOException {
        User user = userRepository.findById(id).orElse(null);
        assert user != null;
        user.setAvatar(fileImport.getOriginalFilename());
        userRepository.save(user);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileImport.getOriginalFilename());
        Files.write(fileNameAndPath, fileImport.getBytes());
    }

    @Override
    public void deleteImg(Long id) throws IOException {
        Files.delete(Paths.get(UPLOAD_DIRECTORY, userRepository.getName(id)));
    }
}
