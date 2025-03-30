package ru.tattoo.maxsim.service.impl;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.GalleryDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.util.ImageUtils;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class ImagesServiceImpl extends AbstractCRUDService<Images, Long> implements ImagesService {

    private static final int PARTITION_SIZE = 3;
    private static final String ALL_GALLERY = "Вся галерея";
    private static final int PAGE_NUMBER = 0;

    @Autowired
    private ImagesRepository imagesRepository;

    @Override
    CrudRepository<Images, Long> getRepository() {
        return imagesRepository;
    }

    @Override
    public void saveImg(MultipartFile fileImport, String description, String category, String userName) throws IOException {
        Images img = new Images();
        img.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        Optional.ofNullable(description).ifPresent(img::setDescription);
        Optional.ofNullable(category).ifPresent(img::setCategory);
        Optional.ofNullable(userName).ifPresent(img::setUserName);

        imagesRepository.save(img);

        ImageUtils.saveImage(fileImport, img.getImageName());
    }

    @Override
    public void deleteImg(Long id) throws IOException {
        Optional<String> imageName = imagesRepository.findNameById(id);
        imageName.ifPresent(name -> {
            try {
                ImageUtils.deleteImage(name);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка удаления файла", e);
            }
        });
        imagesRepository.deleteById(id);
    }

    @Override
    public Page<Images> partition(Pageable p) {
        return imagesRepository.findAll(p);
    }

    @Override
    public Page<Images> partition(String userName, Pageable p) {
        return imagesRepository.findByUserName(userName, p);
    }

    @Override
    public Page<Images> findByCategory(String style, Pageable p) {
        return imagesRepository.findByCategory(style, p);
    }

    @Override
    public boolean bestImage(Images images) {
        Images existingImage = imagesRepository.findById(images.getId())
                .orElseThrow(() -> new EntityNotFoundException("Изображение не найдено"));
        existingImage.setFlag(images.isFlag());
        imagesRepository.save(existingImage);
        return images.isFlag();
    }

    @Override
    public GalleryDTO pageList(String category, Principal principal, int pageSize, int pageNumber) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(pageNumber, pageSize).withSort(sort);

        Page<Images> images;
        if (principal != null && category == null) {
            images = partition(principal.getName(), p);
        } else if (category != null) {
            images = findByCategory(category, p);
        } else {
            images = partition(p); // Все изображения
        }

        List<List<Images>> objects = ImageUtils.partition(
                images.hasContent() ? images.getContent() : Collections.emptyList(),
                PARTITION_SIZE
        );

        return new GalleryDTO(
                objects,
                images.getTotalPages(),
                pageNumber,
                pageSize,
                PageSize.getLisPageSize(),
                images.getTotalElements()
        );
    }

    @Override
    public Iterable<Images> findByFlagTrue() {
        return imagesRepository.findByFlagTrue();
    }
}
