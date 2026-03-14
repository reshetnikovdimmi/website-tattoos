package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.AboutSection;
import ru.tattoo.maxsim.repository.AboutSectionRepository;
import ru.tattoo.maxsim.service.interf.AboutSectionService;
import ru.tattoo.maxsim.storage.ImageStorage;

@Service
public class AboutSectionServiceImpl extends AbstractCRUDService<AboutSection, Long> implements AboutSectionService {

    @Autowired
    private AboutSectionRepository aboutSectionRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    protected String getImageFileName(AboutSection entity) {
        return entity != null ? entity.getImageName() : null;
    }

    @Override
    protected void setImageFileName(AboutSection entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
        }
    }

    @Override
    void prepareObject(AboutSection entity, String fileName) {
        setImageFileName(entity, fileName);
        entity.setSection("home");
    }

    @Override
    CrudRepository<AboutSection, Long> getRepository() {
        return aboutSectionRepository;
    }
}