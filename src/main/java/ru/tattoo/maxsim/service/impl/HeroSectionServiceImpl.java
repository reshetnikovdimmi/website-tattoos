package ru.tattoo.maxsim.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.repository.HomeHeroSectionRepository;
import ru.tattoo.maxsim.service.interf.HeroSectionService;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class HeroSectionServiceImpl extends AbstractCRUDService<HomeHeroSection, Long> implements HeroSectionService {

    @Autowired
    private HomeHeroSectionRepository homeHeroSectionRepository;

    @Autowired
    private ImageStorage imageStorage;  // Внедряем ImageStorage

    // ============= Реализация абстрактных методов AbstractCRUDService =============

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    protected String getImageFileName(HomeHeroSection entity) {
        return entity != null ? entity.getImageName() : null;
    }

    @Override
    protected void setImageFileName(HomeHeroSection entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
        }
    }

    @Override
    void prepareObject(HomeHeroSection entity, String fileName) {
        // Этот метод оставляем для обратной совместимости,
        // но теперь используем setImageFileName
        setImageFileName(entity, fileName);
        entity.setSection("home");
    }

    @Override
    CrudRepository<HomeHeroSection, Long> getRepository() {
        return homeHeroSectionRepository;
    }



}