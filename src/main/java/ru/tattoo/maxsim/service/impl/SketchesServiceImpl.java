package ru.tattoo.maxsim.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.SketchesDTO;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.SketchesRepository;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.storage.ImageStorage;
import ru.tattoo.maxsim.util.ImageUtils;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SketchesServiceImpl extends AbstractCRUDService<Sketches, Long> implements SketchesService {

    private static final int PARTITION_SIZE = 3;

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
    public SketchesDTO getSketchesDto(String category, Principal principal, int pageSize, int pageNumber) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(pageNumber, pageSize).withSort(sort);

        Page<Sketches> images = sketchesRepository.findAll(p);

        List<List<Sketches>> objects = ImageUtils.partition(
                images.hasContent() ? images.getContent() : Collections.emptyList(),
                PARTITION_SIZE
        );

        return new SketchesDTO(
                objects,
                images.getTotalPages(),
                pageNumber,
                pageSize,
                PageSize.getLisPageSize(),
                images.getTotalElements()
        );
    }


}
