package ru.tattoo.maxsim.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Blog;
import ru.tattoo.maxsim.model.SettingWebsite;
import ru.tattoo.maxsim.repository.SettingWebsiteRepository;
import ru.tattoo.maxsim.service.interf.SettingWebsiteService;
import ru.tattoo.maxsim.storage.ImageStorage;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
public class SettingWebsiteServiceImpl extends AbstractCRUDService<SettingWebsite, Long> implements SettingWebsiteService {

    @Autowired
    private SettingWebsiteRepository settingWebsiteRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    @Transactional
    public void create(SettingWebsite entity) {
        if (entity.getId() != null) {
            log.debug("Сохранение сущности SettingWebsite с id: {}", entity.getId());
            Optional<SettingWebsite> savedEntity = getRepository().findById(entity.getId());
            if (savedEntity.isPresent()) {
                // Сохраняем секцию из существующей записи
                entity.setSection(savedEntity.get().getSection());
            }else {
                throw new EntityNotFoundException("Сущность с id " + entity.getId() + " не найдена");
            }
            getRepository().save(entity);
        }else {
            getRepository().save(entity);
        }

        log.debug("Сущность создана: {}", entity);
    }


    @Override
    void prepareObject(SettingWebsite entity, String s) {
        Optional<SettingWebsite> savedEntity = getRepository().findById(entity.getId());
        entity.setSection(savedEntity.get().getSection());
        entity.setImageName(s);
    }

    @Override
    CrudRepository<SettingWebsite, Long> getRepository() {
        return settingWebsiteRepository;
    }

    @Override
    protected String getImageFileName(SettingWebsite entity) {
        return null;
    }

    @Override
    protected void setImageFileName(SettingWebsite entity, String fileName) {

    }


}
