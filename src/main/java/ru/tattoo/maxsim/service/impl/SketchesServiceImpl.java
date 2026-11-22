package ru.tattoo.maxsim.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.SketchesRepository;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.storage.ImageStorage;
import ru.tattoo.maxsim.util.PageRequestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SketchesServiceImpl extends AbstractCRUDService<Sketches, Long> implements SketchesService {

    @Autowired
    private SketchesRepository sketchesRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    public List<Sketches> findLimit() {
        return sketchesRepository.findLimit();
    }

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    void prepareObject(Sketches entity, String s) {
        setImageFileName(entity, s);
        entity.setDate(new Date());
    }

    @Override
    protected CrudRepository<Sketches, Long> getRepository() {
        return sketchesRepository;
    }

    @Override
    protected String getImageFileName(Sketches entity) {
        return entity != null ? entity.getImageName() : null;
    }

    @Override
    protected void setImageFileName(Sketches entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
        }
    }


    @Override
    public PageResult<Sketches> getSketchesPage(int page, int size) {
        int safePage = Math.max(page, 0);
        Page<Sketches> images = sketchesRepository.findAll(PageRequestUtils.of(safePage, size));

        if (safePage > 0 && safePage >= images.getTotalPages()) {
            images = sketchesRepository.findAll(PageRequestUtils.last(images));
        }

        return PageResult.of(images);
    }


}
