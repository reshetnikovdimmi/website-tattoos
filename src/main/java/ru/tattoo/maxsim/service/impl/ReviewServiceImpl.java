package ru.tattoo.maxsim.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
@Slf4j
public class ReviewServiceImpl extends AbstractCRUDService<ReviewsUser,Long> implements ReviewService {

    @Autowired
    private ReviewsUserRepository reviewsUserRepository;

    @Override
    void prepareObject(ReviewsUser entity, String imageName) {
        log.debug("Подготовка объекта ReviewsUser для сохранения");

        // 1. Устанавливаем имя изображения
        entity.setImageName(imageName);

        // 2. БЕЗОПАСНО получаем имя пользователя
        String username = getCurrentUsername();
        entity.setUserName(username);

        // 3. Устанавливаем дату
        entity.setDate(new Date());

        log.info("Объект подготовлен: image={}, user={}, date={}",
                imageName, username, entity.getDate());
    }

    @Override
    public void create(ReviewsUser entity) {
        String username = getCurrentUsername();
        entity.setUserName(username);
        entity.setDate(new Date());
        log.info("Объект подготовлен: image={}, user={}, date={}",
                entity.getImageName(), username, entity.getDate());
        getRepository().save(entity);
    }


    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    CrudRepository<ReviewsUser, Long> getRepository() {
        return reviewsUserRepository;
    }

    @Override
    public void deleteById(Long id) throws IOException {

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

}
