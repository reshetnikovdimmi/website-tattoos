package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Blog;
import java.io.IOException;
import java.util.List;

public interface BlogService extends CRUDService<Blog, Long>{

    void saveInterestingWorks(MultipartFile fileImport, String description) throws IOException;

    void deleteInterestingWorks(Long id) throws IOException;

    List<Blog> findLimit();

    List<Blog> findDescription();

    void saveImg(MultipartFile fileImport, String section, Long id) throws IOException;
}
