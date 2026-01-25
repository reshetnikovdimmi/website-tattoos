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
import ru.tattoo.maxsim.model.DTO.GalleryDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.util.ImageUtils;
import ru.tattoo.maxsim.util.PageSize;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;


@Service
@Slf4j
public class ImagesServiceImpl extends AbstractCRUDService<Images, Long> implements ImagesService {

    private static final int PARTITION_SIZE = 3;

    @Autowired
    private ImagesRepository imagesRepository;

    @Override
    CrudRepository<Images, Long> getRepository() {
        return imagesRepository;
    }

    @Override
    void prepareObject(Images entity, String s) {
        log.debug("Подготовка объекта Images: установка имени изображения: {}", s);
        entity.setImageName(s);
    }
    @Override
    public GalleryDTO getGalleryDto(String category, Principal principal, int pageSize, int pageNumber) {
        log.info("Запрос галереи: category={}, principal={}, pageSize={}, pageNumber={}",
                category, principal != null ? principal.getName() : "null", pageSize, pageNumber);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(pageNumber, pageSize).withSort(sort);

        Page<Images> images = null;
        String fetchStrategy;

        // Определяем стратегию выборки
        if (principal != null && category != null) {
            // Изображения категории для конкретного пользователя
            images = findByCategoryAndUser(category, principal.getName(), p);
            fetchStrategy = "USER_CATEGORY";
        } else if (category != null) {
            // Изображения категории для всех (публичные)
            images = findByCategory(category, p);
            fetchStrategy = "PUBLIC_CATEGORY";
            images.getContent().forEach(img ->
                    log.info("Image[id={}, name='{}', category='{}', user='{}', flag={}, стратегия={}]",
                            img.getId(),
                            img.getImageName(),
                            img.getCategory(),
                            img.getUserName(),
                            img.isFlag(),
                            fetchStrategy
                    )
            );
        } else if (principal != null) {
            // Все изображения пользователя
            images = getPagedImages(principal.getName(), p);
            fetchStrategy = "USER_IMAGES";
        } else {
            // Все публичные изображения
            images = getPagedImages(p);
            fetchStrategy = "ALL_IMAGES";
            images.getContent().forEach(img ->
                    log.info("Image[id={}, name='{}', category='{}', user='{}', flag={}]",
                            img.getId(),
                            img.getImageName(),
                            img.getCategory(),
                            img.getUserName(),
                            img.isFlag()
                    )
            );
        }

        log.info("Получено {} изображений на странице {} из {}",
                images.getNumberOfElements(), pageNumber, images.getTotalElements());

        List<List<Images>> objects = ImageUtils.partition(
                images.hasContent() ? images.getContent() : Collections.emptyList(),
                PARTITION_SIZE
        );
        log.debug("Изображения разделены на {} групп по {} элементов",
                objects.size(), PARTITION_SIZE);

        GalleryDTO result = new GalleryDTO(
                objects,
                images.getTotalPages(),
                pageNumber,
                pageSize,
                PageSize.getLisPageSize(),
                images.getTotalElements()
        );

        log.info("Галерея успешно подготовлена: стратегия={}, всего страниц={}, всего элементов={}",
                fetchStrategy, images.getTotalPages(), images.getTotalElements());

        return result;
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
        log.info("Обновление флага изображения: ID={}, новый флаг={}", id, flag);

        Images images = new Images();
        images.setId(id);
        images.setFlag(flag);

        try {
            boolean result = bestImage(images);
            String message = result ? "Установлено" : "Снято";
            log.info("Флаг успешно обновлен: ID={}, результат={}, сообщение='{}'",
                    id, result, message);
            return message;
        } catch (Exception e) {
            log.error("Ошибка при обновлении флага изображения ID={}: {}", id, e.getMessage(), e);
            throw e;
        }
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
