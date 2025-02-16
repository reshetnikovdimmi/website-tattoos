package ru.tattoo.maxsim.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.Collections;
import java.util.List;


@Service
public class ImagesServiceImpl extends AbstractCRUDService<Images, Long> implements ImagesService {

    private static final int PARTITION_SIZE = 3;

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

    @Override
    public Page<Images> partition(Pageable p) {
        return imagesRepository.findAll(p);
    }

    @Override
    public Page<Images> findByCategory(String style, Pageable p) {
        return imagesRepository.findByCategory(style, p);
    }

    @Override
    public Object pageList(Page<Images> images) {
        List<Images> objects = images.hasContent() ? images.getContent() : Collections.emptyList();
        List<List<Images>> smallerLists = Lists.partition(objects, PARTITION_SIZE);
        return smallerLists;
    }

    @Override
    public boolean bestImage(Images images) {

        Images img = imagesRepository.findById(images.getId()).get();
        img.setFlag(images.isFlag());
        imagesRepository.save(img);

        return images.isFlag();
    }

}
