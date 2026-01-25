package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.AboutSection;
import ru.tattoo.maxsim.repository.AboutSectionRepository;
import ru.tattoo.maxsim.service.interf.AboutSectionService;
import ru.tattoo.maxsim.util.ImageUtils;
import java.io.IOException;
import java.util.Optional;

@Service
public class AboutSectionServiceImpl extends AbstractCRUDService<AboutSection, Long> implements AboutSectionService {
    @Autowired
    private AboutSectionRepository aboutSectionRepository;

    @Override
    void prepareObject(AboutSection entity, String s) {
        entity.setImageName(s);
        entity.setSection("home");
    }

    @Override
    CrudRepository<AboutSection, Long> getRepository() {
        return aboutSectionRepository;
    }


    @Override
    public void deleteById(Long id) {

        Optional<String> imageName = aboutSectionRepository.getName(id);

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
