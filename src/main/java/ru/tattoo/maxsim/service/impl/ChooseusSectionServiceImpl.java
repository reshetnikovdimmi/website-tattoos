package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.ChooseusSection;
import ru.tattoo.maxsim.repository.ChooseusSectionRepository;
import ru.tattoo.maxsim.service.interf.ChooseusSectionService;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.util.Optional;

@Service
public class ChooseusSectionServiceImpl extends AbstractCRUDService<ChooseusSection, Long> implements ChooseusSectionService {

    @Autowired
    private ChooseusSectionRepository chooseusSectionRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    protected String getImageFileName(ChooseusSection entity) {
        return entity != null ? entity.getImageName() : null; // Обратите внимание: iconName
    }

    @Override
    protected void setImageFileName(ChooseusSection entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
        }
    }

    @Override
    void prepareObject(ChooseusSection entity, String fileName) {
        setImageFileName(entity, fileName);
        entity.setSection("home");
    }

    @Override
    CrudRepository<ChooseusSection, Long> getRepository() {
        return chooseusSectionRepository;
    }
    @Override
    public void create(ChooseusSection entity) {

        Optional<ChooseusSection> object = getRepository().findById(entity.getId());

        entity.setSection(object.get().getSection());
        entity.setCategoryTitle(object.get().getCategoryTitle());

        getRepository().save(entity);
    }
}