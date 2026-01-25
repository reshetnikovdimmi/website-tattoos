package ru.tattoo.maxsim.service.impl;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class SettingWebsiteServiceImpl extends AbstractCRUDService<SettingWebsite, Long> implements SettingWebsiteService {

    @Autowired
    private SettingWebsiteRepository settingWebsiteRepository;

    @Override
    void prepareObject(SettingWebsite entity, String s) {
        entity.setImageName(s);
    }

    @Override
    CrudRepository<SettingWebsite, Long> getRepository() {
        return settingWebsiteRepository;
    }



}
