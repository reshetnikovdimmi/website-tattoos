package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Home;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.HomeRepository;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;


@Service
public class HomeServiceImpl extends AbstractCRUDService <Home, Long> implements HomeService {

    @Autowired
    private HomeRepository homeRepository;

    @Override
    CrudRepository<Home, Long> getRepository() {
        return homeRepository;
    }

    @Override
    public void saveImg(MultipartFile fileImport, String category) throws IOException {
        Home home = new Home();
        home.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        homeRepository.save(home);

        ImageUtils.saveImage(fileImport, home.getImageName());
    }

    @Override
    public void deleteImg(Long id) throws IOException {

        Optional<String> imageName = homeRepository.getName(id);

        imageName.ifPresent(name -> {
            try {
                ImageUtils.deleteImage(name);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка удаления файла", e);
            }
        });
        getRepository().deleteById(id);

    }
}
