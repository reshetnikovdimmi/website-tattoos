package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ChooseusSection;
import ru.tattoo.maxsim.model.ClassesSection;
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
    CrudRepository<ChooseusSection, Long> getRepository() {
        return chooseusSectionRepository;
    }

    @Override
    public void saveImg(MultipartFile fileImport, String textH1, String textH2, String textH3) throws IOException {
        ChooseusSection home = new ChooseusSection();
        home.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        home.setTextH1(textH1);
        home.setTextH2(textH2);
        home.setTextH3(textH3);
        home.setSection("home");
        getRepository().save(home);

        ImageUtils.saveImage(fileImport, home.getImageName());
    }
    @Override
    public void deleteImg(Long id) {

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
