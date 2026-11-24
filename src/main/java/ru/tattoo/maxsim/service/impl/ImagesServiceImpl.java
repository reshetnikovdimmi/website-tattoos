package ru.tattoo.maxsim.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tattoo.maxsim.model.DTO.GalleryQuery;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.storage.ImageStorage;
import ru.tattoo.maxsim.util.PageRequestUtils;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ImagesServiceImpl extends AbstractCRUDService<Images, Long> implements ImagesService {

    private final ImageStorage imageStorage;
    private final ImagesRepository imagesRepository;

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
        // Имя файла уже установлено в saveImg() через setImageFileName().
        log.debug("Изображение готово к сохранению: {}", fileName);
    }
    @Override
    @Transactional(readOnly = true)
    public PageResult<Images> getGalleryPage(GalleryQuery query, String username) {
        String category = query.category();
        int safePage = Math.max(query.page(), 0);
        log.debug("Запрос галереи: category={}, username={}, page={}, size={}",
                category, username, safePage, query.size());

        Page<Images> images = fetchImages(category, username, PageRequestUtils.of(safePage, query.size()));

        // page за границей: показываем последнюю существующую страницу
        if (safePage > 0 && safePage >= images.getTotalPages()) {
            log.warn("Страница {} вне диапазона (totalPages={}), переключаемся на последнюю",
                    safePage, images.getTotalPages());
            images = fetchImages(category, username, PageRequestUtils.last(images));
        }

        log.info("Получено {} изображений на странице {} из {}",
                images.getNumberOfElements(), images.getNumber(), images.getTotalElements());

        return PageResult.of(images);
    }

    private Page<Images> fetchImages(String category, String username, Pageable p) {
        Page<Images> images;
        String fetchStrategy;

        if (username != null && category != null) {
            images = findByCategoryAndUser(category, username, p);
            fetchStrategy = "USER_CATEGORY";
        } else if (category != null) {
            images = findByCategory(category, p);
            fetchStrategy = "PUBLIC_CATEGORY";
        } else if (username != null) {
            images = getPagedImages(username, p);
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

    private Page<Images> getPagedImages(Pageable p) {
        log.debug("Запрос всех изображений с пагинацией: page={}, size={}, sort={}",
                p.getPageNumber(), p.getPageSize(), p.getSort());
        return imagesRepository.findAll(p);
    }

    private Page<Images> getPagedImages(String userName, Pageable p) {
        log.debug("Запрос изображений пользователя '{}' с пагинацией: page={}, size={}",
                userName, p.getPageNumber(), p.getPageSize());
        return imagesRepository.findByUserName(userName, p);
    }

    private Page<Images> findByCategory(String style, Pageable p) {
        log.debug("Поиск изображений по категории '{}' с пагинацией: page={}, size={}",
                style, p.getPageNumber(), p.getPageSize());
        return imagesRepository.findByCategory(style, p);
    }

    @Override
    @Transactional
    public void updateImageFlag(Long id, boolean flag) {
        Images image = imagesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Изображение с id " + id + " не найдено"));

        image.setFlag(flag);
        imagesRepository.save(image);

        log.debug("Флаг изображения {} обновлен на {}", id, flag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Images> findByFlagTrue() {
        log.debug("Запрос всех изображений с флагом 'лучшее'");
        List<Images> result = imagesRepository.findByFlagTrue();
        log.debug("Найдено {} изображений с флагом 'лучшее'", result.size());
        return result;
    }
}
