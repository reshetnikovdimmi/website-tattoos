package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.repository.ClassesSectionRepository;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.util.List;

@Service
public class ClassesSectionServiceImpl extends AbstractCRUDService<ClassesSection, Long> implements ClassesSectionService {

    @Autowired
    private ClassesSectionRepository classesSectionRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    protected String getImageFileName(ClassesSection entity) {
        return entity != null ? entity.getImageName() : null;
    }

    @Override
    protected void setImageFileName(ClassesSection entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
        }
    }

    @Override
    void prepareObject(ClassesSection entity, String fileName) {
        setImageFileName(entity, fileName);
        entity.setSection("home");
    }

    @Override
    CrudRepository<ClassesSection, Long> getRepository() {
        return classesSectionRepository;
    }

    @Override
    public List<ClassesSection> findByTitle(String s) {
        return classesSectionRepository.findByCategoryTitle(s);
    }

    @Override
    public void create(ClassesSection entity) {
        entity.setSection("home");
        entity.setCategoryTitle("category_title");
        getRepository().save(entity);
    }
}