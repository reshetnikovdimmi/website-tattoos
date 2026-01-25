package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Blog;
import ru.tattoo.maxsim.repository.BlogRepository;
import ru.tattoo.maxsim.service.interf.BlogService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BlogServiceImpl extends AbstractCRUDService<Blog, Long> implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    void prepareObject(Blog entity, String s) {
        entity.setImageName(s);
        entity.setDate(new Date());
    }
    @Override
    CrudRepository<Blog, Long> getRepository() {
        return blogRepository;
    }

    @Override
    public void deleteById(Long id) throws IOException {

        Optional<String> imageName = blogRepository.findNameById(id);
        imageName.ifPresent(name -> {
            try {
                ImageUtils.deleteImage(name);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка удаления файла", e);
            }
        });
        blogRepository.deleteById(id);
    }

    @Override
    public List<Blog> findLimit() {
        return blogRepository.findTop4ByDescriptionIsNotNullOrderByDateDesc();
    }

    @Override
    public List<Blog> findDescription() {
        return blogRepository.findByDescriptionIsNotNullOrderByDateDesc();    }
}
