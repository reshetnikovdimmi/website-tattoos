package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Commits;
import ru.tattoo.maxsim.model.ReviewsUser;

import java.io.IOException;

public interface CommitsService extends CRUDService<Commits,Long>{
    void saveImd(String comment, String name) throws IOException;
}
