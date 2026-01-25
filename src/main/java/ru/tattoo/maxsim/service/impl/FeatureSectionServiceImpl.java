package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.FeatureSection;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.repository.FeatureSectionRepository;
import ru.tattoo.maxsim.service.interf.FeatureSectionService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.util.Optional;

@Service
public class FeatureSectionServiceImpl extends AbstractCRUDService<FeatureSection, Long> implements FeatureSectionService {

    @Autowired
    private FeatureSectionRepository featureSectionRepository;

    @Override
    void prepareObject(FeatureSection entity, String s) {
        entity.setImageName(s);
        entity.setSection("home");
    }

    @Override
    CrudRepository<FeatureSection, Long> getRepository() {
        return featureSectionRepository;
    }


    @Override
    public void deleteById(Long id) {

        Optional<String> imageName = featureSectionRepository.getName(id);

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
