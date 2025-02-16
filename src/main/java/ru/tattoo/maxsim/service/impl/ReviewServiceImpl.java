package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.repository.ReviewsUserRepository;
import ru.tattoo.maxsim.service.interf.ReviewService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;


@Service
public class ReviewServiceImpl extends AbstractCRUDService<ReviewsUser,Long> implements ReviewService {

    @Autowired
    private ReviewsUserRepository reviewsUserRepository;

    @Override
    CrudRepository<ReviewsUser, Long> getRepository() {
        return reviewsUserRepository;
    }

    @Override
    public void deleteImg(Long id) throws IOException {
        Files.delete(Paths.get(UPLOAD_DIRECTORY, reviewsUserRepository.getName(id)));
        reviewsUserRepository.deleteById(id);
    }

    @Override
    public List<ReviewsUser> findLimit() {
        return reviewsUserRepository.findLimit();
    }

    @Override
    public int getCount() {
        return reviewsUserRepository.getCount();
    }

    @Override
    public void saveImd(MultipartFile fileImport, String comment, String name) throws IOException {
        ReviewsUser reviewsUser = new ReviewsUser();
        reviewsUser.setImageName(fileImport.getOriginalFilename());
        reviewsUser.setComment(comment);
        reviewsUser.setUserName(name);
        reviewsUser.setDate(new Date());

        reviewsUserRepository.save(reviewsUser);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY , fileImport.getOriginalFilename());
        Files.write(fileNameAndPath, fileImport.getBytes());
    }


}
