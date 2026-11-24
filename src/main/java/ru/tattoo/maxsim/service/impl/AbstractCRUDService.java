package ru.tattoo.maxsim.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.exceptions.FileUploadException;
import ru.tattoo.maxsim.exceptions.ValidationException;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.storage.ImageStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public abstract class AbstractCRUDService<E, K> implements CRUDService<E, K> {

    // Абстрактный метод для получения ImageStorage
    protected abstract ImageStorage getImageStorage();

    abstract void prepareObject(E entity, String fileName);
    abstract CrudRepository<E, K> getRepository();

    // Новый метод - для получения имени файла из сущности
    protected abstract String getImageFileName(E entity);

    // Новый метод - для установки имени файла в сущность
    protected abstract void setImageFileName(E entity, String fileName);

    @Override
    @Transactional
    public void create(E entity) {
        getRepository().save(entity);
        log.debug("Сущность создана: {}", entity);
    }

    @Override
    public E findById(K id) {
        return getRepository().findById(id).orElse(null);
    }

    @Override
    public List<E> findAll() {
        List<E> entities = StreamSupport
                .stream(getRepository().findAll().spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(entities);
        return entities;
    }

    @Override
    @Transactional
    public E update(E entity) {
        E savedEntity = getRepository().save(entity);
        log.debug("Сущность обновлена: {}", savedEntity);
        return savedEntity;
    }

    @Override
    public void delete(E entity) {
        getRepository().delete(entity);
        deleteFileQuietly(getImageFileName(entity));
        log.debug("Сущность удалена: {}", entity);
    }

    @Override
    public void deleteById(K id) throws IOException {
        E entity = getRepository().findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка удалить несуществующую сущность с id {}", id);
                    return new EntityNotFoundException("Сущность с id " + id + " не найдена");
                });

        // Источник истины — БД. Не оборачиваем метод в общую транзакцию:
        // repository.delete() коммитится до удаления файла. Если файл удалить
        // не удалось, его подберёт очистка сирот.
        getRepository().delete(entity);
        deleteFileQuietly(getImageFileName(entity));
        log.debug("Сущность с ID {} удалена", id);
    }

    @Override
    public void deleteAll() {
        List<E> entities = findAll();
        getRepository().deleteAll();
        for (E entity : entities) {
            deleteFileQuietly(getImageFileName(entity));
        }
        log.debug("Все сущности удалены");
    }

    @Override
    @Transactional
    public List<E> saveAll(List<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        Iterable<E> savedEntities = getRepository().saveAll(entities);
        log.debug("Сохранено {} сущностей", entities.size());

        return StreamSupport.stream(savedEntities.spliterator(), false).toList();
    }

    @Override
    public E saveImg(MultipartFile fileImport, E entity) throws IOException {
        log.info("💾 Начало сохранения изображения");
        log.info("   Файл: '{}'", fileImport.getOriginalFilename());
        log.info("   Размер: {} байт", fileImport.getSize());
        log.info("   Тип: {}", fileImport.getContentType());

        validateFile(fileImport);

        String savedFileName = null;
        boolean persisted = false;

        try {
            // Убираем генерацию имени здесь - пусть Storage сам генерирует!
            // String uniqueFileName = getImageStorage().generateUniqueFileName(
            //     fileImport.getOriginalFilename()
            // );

            // Просто передаем оригинальное имя
            String originalFileName = fileImport.getOriginalFilename();
            log.debug("📄 Оригинальное имя файла: {}", originalFileName);

            // Сохраняем файл через ImageStorage (он сам сгенерирует имя)
            String uniqueFileName = getImageStorage().generateUniqueFileName(originalFileName);
            savedFileName = getImageStorage().saveImage(fileImport, uniqueFileName);
            log.debug("💾 Файл сохранен в хранилище с именем: {}", savedFileName);

            // Устанавливаем имя файла в сущность
            setImageFileName(entity, savedFileName);
            prepareObject(entity, savedFileName);

            log.debug("🔄 Объект подготовлен: {}", entity);

            // Сохраняем сущность в БД
            E savedEntity = getRepository().save(entity);
            persisted = true;
            log.info("✅ Изображение успешно сохранено!");
            log.info("   Имя файла: {}", savedFileName);
            log.info("   Сущность ID: {}", getEntityId(savedEntity));

            return savedEntity;

        } catch (IOException e) {
            if (!persisted) {
                deleteFileQuietly(savedFileName);
            }
            log.error("❌ Ошибка ввода-вывода при сохранении изображения: {}", e.getMessage(), e);
            throw new FileUploadException("Ошибка сохранения изображения: " + e.getMessage(), e);
        } catch (Exception e) {
            if (!persisted) {
                deleteFileQuietly(savedFileName);
            }
            log.error("❌ Неожиданная ошибка при сохранении изображения", e);
            throw new FileUploadException("Неожиданная ошибка при сохранении изображения", e);
        }
    }

    /**
     * Валидация загружаемого файла
     */
    private void validateFile(MultipartFile file) {
        if (file == null) {
            throw new ValidationException("Файл не может быть null");
        }

        if (file.isEmpty()) {
            throw new ValidationException("Файл не может быть пустым");
        }

        // Проверка расширения
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new ValidationException("Имя файла не может быть пустым");
        }

        // Проверка на допустимые типы (можно вынести в конфиг)
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new ValidationException("Файл должен быть изображением");
        }
    }

    /**
     * Удаляет файл, не прерывая операцию: если файл не удалился,
     * его подберёт периодическая очистка сирот.
     */
    private void deleteFileQuietly(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        try {
            getImageStorage().deleteImage(fileName);
            log.debug("Файл удалён: {}", fileName);
        } catch (IOException e) {
            log.warn("Не удалось удалить файл {}: {}. Будет очищен планировщиком.",
                    fileName, e.getMessage());
        }
    }

    /**
     * Получение ID сущности (для логирования)
     */
    @SuppressWarnings("unchecked")
    private K getEntityId(E entity) {
        try {
            // Пробуем получить ID через рефлексию (упрощенно)
            var method = entity.getClass().getMethod("getId");
            return (K) method.invoke(entity);
        } catch (Exception e) {
            return null;
        }
    }
}
