package ru.tattoo.maxsim.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.SketchesRepository;
import ru.tattoo.maxsim.service.interf.SketchesService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;


@Service
public class SketchesServiceImpl extends AbstractCRUDService<Sketches, Long> implements SketchesService {

    private static final int PARTITION_SIZE = 3;

    @Autowired
    private SketchesRepository sketchesRepository;

    @Override
    CrudRepository<Sketches, Long> getRepository() {
        return sketchesRepository;
    }

    @Override
    public void saveImg(MultipartFile fileImport, String description) throws IOException {
        Sketches sketches = new Sketches();
        sketches.setImageName(fileImport.getOriginalFilename());
        sketches.setDescription(description);

        sketchesRepository.save(sketches);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY , fileImport.getOriginalFilename());
        Files.write(fileNameAndPath, fileImport.getBytes());
    }

    @Override
    public void deleteImg(Long id) throws IOException {
        Files.delete(Paths.get(UPLOAD_DIRECTORY, sketchesRepository.getName(id)));
        sketchesRepository.deleteById(id);
    }

    @Override
    public Page<Sketches> partition(Pageable p) {
        return sketchesRepository.findAll(p);
    }

    @Override
    public Object pageList(Page<Sketches> images) {
        List<Sketches> objects = images.hasContent() ? images.getContent() : Collections.emptyList();
        List<List<Sketches>> smallerLists = Lists.partition(objects, PARTITION_SIZE);
        return smallerLists;
    }


}
