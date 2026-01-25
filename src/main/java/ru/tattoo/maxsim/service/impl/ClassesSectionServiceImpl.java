package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.AboutSection;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.repository.ClassesSectionRepository;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ClassesSectionServiceImpl extends AbstractCRUDService<ClassesSection, Long> implements ClassesSectionService {

    @Autowired
    private ClassesSectionRepository classesSectionRepository;

    @Override
    void prepareObject(ClassesSection entity, String s) {
        entity.setImageName(s);
        entity.setSection("home");
    }

    @Override
    public void create(ClassesSection entity) {
        entity.setSection("home");
        entity.setTitle("title");
        getRepository().save(entity);
    }

    @Override
    CrudRepository<ClassesSection, Long> getRepository() {
        return classesSectionRepository;
    }

    @Override
    public List<ClassesSection> findByTitle(String s) {
        return classesSectionRepository.findByTitle(s);
    }

    @Override
    public void deleteById(Long id) {

        Optional<String> imageName = classesSectionRepository.getName(id);

        imageName.ifPresent(name -> {
            try {
                ImageUtils.deleteImage(name);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка удаления файла", e);
            }
        });
        getRepository().deleteById(id);
    }
}
