package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Home;
import ru.tattoo.maxsim.repository.HomeRepository;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
public class HomeServiceImpl extends AbstractCRUDService <Home, Long> implements HomeService {

    @Autowired
    private HomeRepository homeRepository;

    @Override
    void prepareObject(Home entity, String s) {

    }

    @Override
    CrudRepository<Home, Long> getRepository() {
        return homeRepository;
    }


}
