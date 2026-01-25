package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.HomeHeroSection;
import ru.tattoo.maxsim.repository.HomeHeroSectionRepository;
import ru.tattoo.maxsim.service.interf.HeroSectionService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.util.Optional;

@Service
public class HeroSectionServiceImpl extends AbstractCRUDService<HomeHeroSection, Long> implements HeroSectionService {

    @Autowired
    public HomeHeroSectionRepository homeHeroSectionRepository;

    /**
     * @param entity
     * @param s
     */
    @Override
    void prepareObject(HomeHeroSection entity, String s) {
        entity.setImageName(s);
        entity.setSection("home");
    }

    @Override
    CrudRepository<HomeHeroSection, Long> getRepository() {
        return homeHeroSectionRepository;
    }

    @Override
    public void deleteById(Long id) {

        Optional<String> imageName = homeHeroSectionRepository.getName(id);

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
