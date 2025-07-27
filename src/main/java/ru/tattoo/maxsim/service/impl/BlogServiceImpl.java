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
    CrudRepository<Blog, Long> getRepository() {
        return blogRepository;
    }

    @Override
    public void saveInterestingWorks(MultipartFile fileImport, String description) throws IOException {
        Blog interestingWorks = new Blog();
        interestingWorks.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        Optional.ofNullable(description).ifPresent(interestingWorks::setDescription);
        interestingWorks.setDate(new Date());

        blogRepository.save(interestingWorks);

        ImageUtils.saveImage(fileImport, interestingWorks.getImageName());

    }


    public void saveImg(MultipartFile fileImport, String section, Long id) throws IOException {
        Blog home = new Blog();
        home.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        home.setSection(section);
        home.setId(id);

        getRepository().save(home);

        ImageUtils.saveImage(fileImport, home.getImageName());
    }

    @Override
    public void deleteInterestingWorks(Long id) throws IOException {

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
