package ru.tattoo.maxsim.service.impl;

import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Service
public class ImagesServiceImpl extends AbstractCRUDService<Images, Long> implements ImagesService {

    @Autowired
    private ImagesRepository imagesRepository;

    @Override
    CrudRepository<Images, Long> getRepository() {
        return imagesRepository;
    }

    @Override
    public void saveImg(MultipartFile fileImport, String description, String category) throws IOException {

        Images img = new Images();
        img.setImageName(fileImport.getOriginalFilename());
        img.setDescription(description);
        img.setCategory(category);

        imagesRepository.save(img);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY , fileImport.getOriginalFilename());
        Files.write(fileNameAndPath, fileImport.getBytes());

    }

    @Override
    public void deleteImg(Long id) throws IOException {
        Files.delete(Paths.get(UPLOAD_DIRECTORY, imagesRepository.getName(id)));

        imagesRepository.deleteById(id);
    }

}
