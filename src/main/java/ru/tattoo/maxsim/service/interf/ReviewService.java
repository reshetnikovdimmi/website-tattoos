package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ReviewsUser;
import java.io.IOException;
import java.util.List;

public interface ReviewService extends CRUDService<ReviewsUser,Long>{


    List<ReviewsUser> findLimit();

    int getCount();


}
