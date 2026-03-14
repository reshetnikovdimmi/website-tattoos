package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.Home;
import ru.tattoo.maxsim.repository.HomeRepository;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.storage.ImageStorage;

@Service
public class HomeServiceImpl extends AbstractCRUDService<Home, Long> implements HomeService {

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    protected String getImageFileName(Home entity) {
        return  null; // Обратите внимание: image
    }

    @Override
    protected void setImageFileName(Home entity, String fileName) {

    }

    @Override
    void prepareObject(Home entity, String fileName) {
        setImageFileName(entity, fileName);
    }

    @Override
    CrudRepository<Home, Long> getRepository() {
        return homeRepository;
    }
}