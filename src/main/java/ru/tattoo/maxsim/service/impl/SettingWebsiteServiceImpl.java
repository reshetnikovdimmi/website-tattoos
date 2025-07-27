package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Blog;
import ru.tattoo.maxsim.model.SettingWebsite;
import ru.tattoo.maxsim.repository.SettingWebsiteRepository;
import ru.tattoo.maxsim.service.interf.SettingWebsiteService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;

@Service
public class SettingWebsiteServiceImpl extends AbstractCRUDService<SettingWebsite, Long> implements SettingWebsiteService {

    @Autowired
    private SettingWebsiteRepository settingWebsiteRepository;

    @Override
    CrudRepository<SettingWebsite, Long> getRepository() {
        return settingWebsiteRepository;
    }

    public void saveImg(MultipartFile fileImport, String section, Long id) throws IOException {
        SettingWebsite home = new SettingWebsite();
        home.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        home.setSection(section);
        home.setId(id);

        getRepository().save(home);

        ImageUtils.saveImage(fileImport, home.getImageName());
    }
}
