package ru.tattoo.maxsim.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class AbstractCRUDService<E, K> implements CRUDService<E, K>{

    abstract void prepareObject(E entity, String s);
    abstract CrudRepository<E, K> getRepository();

    @Override
    public void create(E entity) {
        getRepository().save(entity);
    }

    @Override
    public E findById(K id) {
        return getRepository().findById(id).orElse(null);
    }

    @Override
    public List<E> findAll() {
        List<E> entities = (List<E>) getRepository().findAll();
        Collections.reverse(entities);
        return entities;
    }

    @Override
    public E update(E entity){
    getRepository().save(entity);
        return entity;
    }

    @Override
    public void delete(E entity) {
        getRepository().delete(entity);
    }

    @Override
    public void deleteById(K id) throws IOException {
        getRepository().deleteById(id);
    }

    @Override
    public void deleteAll() {
        getRepository().deleteAll();
    }

    @Override
    public List<E> saveAll(List<E> l) {
        return List.of();
    }

    @Override
    public void saveImg(MultipartFile fileImport, E entity) throws IOException {
        log.info("Сохранение изображения: файл={}, размер={} байт",
                fileImport.getOriginalFilename(),
                fileImport.getSize());

        try {
            // Генерация уникального имени файла
            String uniqueFileName = ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename());
            log.debug("Сгенерировано уникальное имя файла: {}", uniqueFileName);

            // Подготовка объекта
            prepareObject(entity, uniqueFileName);
            log.debug("Объект подготовлен для сохранения");

            // Сохранение изображения на диск
            ImageUtils.saveImage(fileImport, uniqueFileName);
            log.debug("Изображение сохранено на диск: {}", uniqueFileName);

            // Сохранение сущности в БД
            getRepository().save(entity);
            log.info("Изображение успешно сохранено: имя файла={}, сущность={}",
                    uniqueFileName, entity);

        } catch (IOException e) {
            log.error("Ошибка при сохранении изображения {}: {}",
                    fileImport.getOriginalFilename(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сохранении изображения: {}", e.getMessage(), e);
            throw new IOException("Ошибка сохранения изображения", e);
        }
    }
}
