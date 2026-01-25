package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ChooseusSection;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.repository.ChooseusSectionRepository;
import ru.tattoo.maxsim.service.interf.ChooseusSectionService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.util.Optional;

@Service
public class ChooseusSectionServiceImpl extends AbstractCRUDService<ChooseusSection, Long> implements ChooseusSectionService {

    @Autowired
    private ChooseusSectionRepository chooseusSectionRepository;

    @Override
    public void create(ChooseusSection entity) {

        Optional<ChooseusSection> object = getRepository().findById(entity.getId());

        entity.setSection(object.get().getSection());
        entity.setTitle(object.get().getTitle());

        getRepository().save(entity);
    }

    @Override
    void prepareObject(ChooseusSection entity, String s) {
        entity.setImageName(s);
        entity.setSection("home");
    }

    @Override
    CrudRepository<ChooseusSection, Long> getRepository() {
        return chooseusSectionRepository;
    }

    @Override
    public void deleteById(Long id) {

        Optional<String> imageName = chooseusSectionRepository.getName(id);

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
