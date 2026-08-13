package ru.tattoo.maxsim.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.Video;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.repository.VideoRepository;
import ru.tattoo.maxsim.service.interf.VideoService;
import ru.tattoo.maxsim.storage.ImageStorage;

@Slf4j
@Service
public class VideoServiceImpl extends AbstractCRUDService <Video, Long> implements VideoService {

    @Autowired
    private ImageStorage imageStorage;

    @Autowired
    private VideoRepository videoRepository;


    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    CrudRepository<Video, Long> getRepository() {
        return videoRepository;
    }

    @Override
    void prepareObject(Video entity, String fileName) {
        entity.setHomeId(1L);
    }

    @Override
    protected String getImageFileName(Video entity) {
        return entity != null ? entity.getPreviewImage() : null;
    }

    @Override
    protected void setImageFileName(Video entity, String fileName) {
        if (entity != null) {
            entity.setPreviewImage(fileName);
        }
    }
}
