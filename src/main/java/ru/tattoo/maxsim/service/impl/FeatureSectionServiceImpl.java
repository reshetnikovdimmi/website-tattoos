package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.FeatureSection;
import ru.tattoo.maxsim.repository.FeatureSectionRepository;
import ru.tattoo.maxsim.service.interf.FeatureSectionService;
import ru.tattoo.maxsim.storage.ImageStorage;

@Service
public class FeatureSectionServiceImpl extends AbstractCRUDService<FeatureSection, Long> implements FeatureSectionService {

    @Autowired
    private FeatureSectionRepository featureSectionRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    protected String getImageFileName(FeatureSection entity) {
        return entity != null ? entity.getImageName() : null; // Обратите внимание: iconName
    }

    @Override
    protected void setImageFileName(FeatureSection entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
            entity.setSection("home");
        }
    }

    @Override
    void prepareObject(FeatureSection entity, String fileName) {
        setImageFileName(entity, fileName);
    }

    @Override
    CrudRepository<FeatureSection, Long> getRepository() {
        return featureSectionRepository;
    }
}