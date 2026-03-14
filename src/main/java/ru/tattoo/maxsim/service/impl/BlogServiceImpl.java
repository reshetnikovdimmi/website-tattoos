package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.Blog;
import ru.tattoo.maxsim.repository.BlogRepository;
import ru.tattoo.maxsim.service.interf.BlogService;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.util.Date;
import java.util.List;

@Service
public class BlogServiceImpl extends AbstractCRUDService<Blog, Long> implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    protected String getImageFileName(Blog entity) {
        return entity != null ? entity.getImageName() : null;
    }

    @Override
    protected void setImageFileName(Blog entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
        }
    }

    @Override
    void prepareObject(Blog entity, String fileName) {
        setImageFileName(entity, fileName);
        entity.setDate(new Date());
    }

    @Override
    CrudRepository<Blog, Long> getRepository() {
        return blogRepository;
    }

    @Override
    public List<Blog> findLimit() {
        return blogRepository.findTop4ByDescriptionIsNotNullOrderByDateDesc();
    }

    @Override
    public List<Blog> findDescription() {
        return blogRepository.findByDescriptionIsNotNullOrderByDateDesc();
    }
}