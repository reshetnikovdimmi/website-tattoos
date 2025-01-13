package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Home;
import ru.tattoo.maxsim.repository.HomeRepository;
import ru.tattoo.maxsim.service.interf.HomeService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        home.setImageName(fileImport.getOriginalFilename());
        homeRepository.save(home);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY , fileImport.getOriginalFilename());
        Files.write(fileNameAndPath, fileImport.getBytes());
    }

    @Override
    public void deleteImg(Long id) throws IOException {
        Files.delete(Paths.get(UPLOAD_DIRECTORY, homeRepository.getName(id)));
        homeRepository.deleteById(id);
    }
}
