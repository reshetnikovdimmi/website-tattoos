package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.repository.ReviewsUserRepository;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;


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

        Optional<String> imageName = reviewsUserRepository.findNameById(id);
        imageName.ifPresent(name -> {
            try {
                ImageUtils.deleteImage(name);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка удаления файла", e);
            }
        });
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
    public void saveImd(String fileName, String comment, String name) throws IOException {
        ReviewsUser reviewsUser = new ReviewsUser();
        reviewsUser.setImageName(fileName);
        reviewsUser.setComment(comment);
        reviewsUser.setUserName(name);
        reviewsUser.setDate(new Date());

        reviewsUserRepository.save(reviewsUser);

    }


}
