package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Blog;
import java.io.IOException;
import java.util.List;

public interface BlogService extends CRUDService<Blog, Long>{

    List<Blog> findLimit();

    List<Blog> findDescription();

}
