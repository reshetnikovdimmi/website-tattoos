package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.repository.PriceSectionRepository;
import ru.tattoo.maxsim.service.interf.PriceSectionService;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.util.Optional;

@Service
public class PriceSectionServiceImpl extends AbstractCRUDService<PriceSection, Long> implements PriceSectionService {

    @Autowired
    private PriceSectionRepository priceSectionRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    protected String getImageFileName(PriceSection entity) {
        return entity != null ? entity.getImageName() : null; // Обратите внимание: iconName
    }

    @Override
    protected void setImageFileName(PriceSection entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
        }
    }

    @Override
    void prepareObject(PriceSection entity, String fileName) {
        setImageFileName(entity, fileName);
    }

    @Override
    CrudRepository<PriceSection, Long> getRepository() {
        return priceSectionRepository;
    }
    @Override
    public void create(PriceSection entity) {

        Optional<PriceSection> object = getRepository().findById(entity.getId());

        entity.setSection(object.get().getSection());
        entity.setTitle(object.get().getTitle());

        getRepository().save(entity);
    }
}