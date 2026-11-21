package ru.tattoo.maxsim.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.storage.ImageStorage;
import ru.tattoo.maxsim.util.PageRequestUtils;
import ru.tattoo.maxsim.util.PageSize;
import ru.tattoo.maxsim.util.PaginationUtils;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;


@Service
@Slf4j
public class ImagesServiceImpl extends AbstractCRUDService<Images, Long> implements ImagesService {

    private static final int PARTITION_SIZE = 3;

    @Autowired
    private ImageStorage imageStorage;

    @Autowired
    private ImagesRepository imagesRepository;

    @Override
    CrudRepository<Images, Long> getRepository() {
        return imagesRepository;
    }

    @Override
    protected String getImageFileName(Images entity) {
        return entity != null ? entity.getImageName() : null;
    }

    @Override
    protected void setImageFileName(Images entity, String fileName) {
        if (entity != null) {
            entity.setImageName(fileName);
        }
    }


    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    void prepareObject(Images entity, String fileName) {
        log.debug("Подготовка объекта Images: установка имени изображения: {}", fileName);
        setImageFileName(entity, fileName);
        entity.setImageName(fileName);
    }
    @Override
    public PageResult<Images> getGalleryPage(String category, Principal principal, int page, int size) {
        log.info("Запрос галереи: category={}, principal={}, page={}, size={}",
                category, principal != null ? principal.getName() : "null", page, size);

        int safePage = Math.max(page, 0);
        Page<Images> images = fetchImages(category, principal, PageRequestUtils.of(safePage, size));

        // page за границей: показываем последнюю существующую страницу
        if (safePage > 0 && safePage >= images.getTotalPages()) {
            log.warn("Страница {} вне диапазона (totalPages={}), переключаемся на последнюю",
                    safePage, images.getTotalPages());
            images = fetchImages(category, principal, PageRequestUtils.last(images));
        }

        log.info("Получено {} изображений на странице {} из {}",
                images.getNumberOfElements(), images.getNumber(), images.getTotalElements());

        return PageResult.of(images, PARTITION_SIZE);
    }

    private Page<Images> fetchImages(String category, Principal principal, Pageable p) {
        Page<Images> images;
        String fetchStrategy;

        if (principal != null && category != null) {
            images = findByCategoryAndUser(category, principal.getName(), p);
            fetchStrategy = "USER_CATEGORY";
        } else if (category != null) {
            images = findByCategory(category, p);
            fetchStrategy = "PUBLIC_CATEGORY";
        } else if (principal != null) {
            images = getPagedImages(principal.getName(), p);
            fetchStrategy = "USER_IMAGES";
        } else {
            images = getPagedImages(p);
            fetchStrategy = "ALL_IMAGES";
        }

        log.debug("Стратегия выборки: {}, page={}, size={}, total={}",
                fetchStrategy, images.getNumber(), images.getSize(), images.getTotalElements());
        return images;
    }

    private Page<Images> findByCategoryAndUser(String category, String name, Pageable p) {
        log.debug("Запрос изображений пользователя '{}' по категории'{}' с пагинацией: page={}, size={}",
                name, category, p.getPageNumber(), p.getPageSize());
        return imagesRepository.findByCategoryAndUser(category,name,p);
    }

    @Override
    public Page<Images> getPagedImages(Pageable p) {
        log.debug("Запрос всех изображений с пагинацией: page={}, size={}, sort={}",
                p.getPageNumber(), p.getPageSize(), p.getSort());
        return imagesRepository.findAll(p);
    }

    @Override
    public Page<Images> getPagedImages(String userName, Pageable p) {
        log.debug("Запрос изображений пользователя '{}' с пагинацией: page={}, size={}",
                userName, p.getPageNumber(), p.getPageSize());
        return imagesRepository.findByUserName(userName, p);
    }

    @Override
    public Page<Images> findByCategory(String style, Pageable p) {
        log.debug("Поиск изображений по категории '{}' с пагинацией: page={}, size={}",
                style, p.getPageNumber(), p.getPageSize());
        return imagesRepository.findByCategory(style, p);
    }

    @Override
    public boolean bestImage(Images images) {
        log.info("Обновление флага 'лучшее' для изображения ID={}, новый флаг={}",
                images.getId(), images.isFlag());

        Images existingImage = imagesRepository.findById(images.getId())
                .orElseThrow(() -> {
                    log.error("Изображение с ID={} не найдено", images.getId());
                    return new EntityNotFoundException("Изображение не найдено");
                });

        boolean oldFlag = existingImage.isFlag();
        existingImage.setFlag(images.isFlag());
        imagesRepository.save(existingImage);

        log.info("Флаг обновлен: изображение ID={}, было={}, стало={}",
                images.getId(), oldFlag, images.isFlag());

        return images.isFlag();
    }

    // 🔄 Новый метод для обновления флага изображения
    public String updateImageFlag(Long id, boolean flag) {
        Images image = imagesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Изображение с id " + id + " не найдено"));

        image.setFlag(flag);
        imagesRepository.save(image);

        log.debug("Флаг изображения {} обновлен на {}", id, flag);
        return "Флаг обновлен на: " + flag;

    }

    @Override
    public Iterable<Images> findByFlagTrue() {
        log.debug("Запрос всех изображений с флагом 'лучшее'");
        Iterable<Images> result = imagesRepository.findByFlagTrue();

        // Подсчет количества
        long count = StreamSupport.stream(result.spliterator(), false).count();
        log.debug("Найдено {} изображений с флагом 'лучшее'", count);

        return result;
    }
}
