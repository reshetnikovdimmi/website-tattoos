package ru.tattoo.maxsim.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.ReviewSection;
import ru.tattoo.maxsim.repository.ReviewSectionRepository;
import ru.tattoo.maxsim.service.interf.ReviewSectionService;
import ru.tattoo.maxsim.storage.ImageStorage;

@Service
@Slf4j
public class ReviewSectionServiceImpl extends AbstractCRUDService <ReviewSection, Long> implements ReviewSectionService {

    @Autowired
    private ReviewSectionRepository reviewSectionRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    void prepareObject(ReviewSection entity, String fileName) {
        entity.setHomeId(1L);
    }

    @Override
    CrudRepository<ReviewSection, Long> getRepository() {
        return reviewSectionRepository;
    }

    @Override
    protected String getImageFileName(ReviewSection entity) {
        return entity != null ? entity.getImageName() : null; // Обратите внимание: image
    }

    @Override
    protected void setImageFileName(ReviewSection entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
        }
    }
}
