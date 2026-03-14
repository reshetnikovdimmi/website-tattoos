package ru.tattoo.maxsim.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.storage.ImageStorage;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
        List<E> entities = (List<E>) getRepository().findAll();
        Collections.reverse(entities);
        return entities;
    }

    @Override
    public E update(E entity) {
        E savedEntity = getRepository().save(entity);
        log.debug("Сущность обновлена: {}", savedEntity);
        return savedEntity;
    }

    @Override
    public void delete(E entity) {
        // Сначала удаляем файл, если есть
        String fileName = getImageFileName(entity);
        if (fileName != null && !fileName.isEmpty()) {
            try {
                getImageStorage().deleteImage(fileName);
                log.debug("Файл удален при удалении сущности: {}", fileName);
            } catch (IOException e) {
                log.error("Ошибка при удалении файла {}: {}", fileName, e.getMessage());
                // Не пробрасываем исключение, чтобы сущность всё равно удалилась
            }
        }

        getRepository().delete(entity);
        log.debug("Сущность удалена: {}", entity);
    }

    @Override
    public void deleteById(K id) throws IOException {
        // Пытаемся найти сущность, чтобы получить имя файла
        E entity = findById(id);
        if (entity != null) {
            String fileName = getImageFileName(entity);
            if (fileName != null && !fileName.isEmpty()) {
                getImageStorage().deleteImage(fileName);
                log.debug("Файл удален при удалении по ID: {}", fileName);
            }
        }

        getRepository().deleteById(id);
        log.debug("Сущность с ID {} удалена", id);
    }

    @Override
    public void deleteAll() {
        // Получаем все сущности и удаляем файлы
        List<E> entities = findAll();
        for (E entity : entities) {
            String fileName = getImageFileName(entity);
            if (fileName != null && !fileName.isEmpty()) {
                try {
                    getImageStorage().deleteImage(fileName);
                    log.debug("Файл удален при массовом удалении: {}", fileName);
                } catch (IOException e) {
                    log.error("Ошибка при удалении файла {}: {}", fileName, e.getMessage());
                }
            }
        }

        getRepository().deleteAll();
        log.debug("Все сущности удалены");
    }

    @Override
    public List<E> saveAll(List<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        Iterable<E> savedEntities = getRepository().saveAll(entities);
        log.debug("Сохранено {} сущностей", entities.size());

        return (List<E>) savedEntities;
    }

    @Override
    public E saveImg(MultipartFile fileImport, E entity) throws IOException {
        log.info("💾 Начало сохранения изображения");
        log.info("   Файл: '{}'", fileImport.getOriginalFilename());
        log.info("   Размер: {} байт", fileImport.getSize());
        log.info("   Тип: {}", fileImport.getContentType());

        validateFile(fileImport);

        try {
            // Убираем генерацию имени здесь - пусть Storage сам генерирует!
            // String uniqueFileName = getImageStorage().generateUniqueFileName(
            //     fileImport.getOriginalFilename()
            // );

            // Просто передаем оригинальное имя
            String originalFileName = fileImport.getOriginalFilename();
            log.debug("📄 Оригинальное имя файла: {}", originalFileName);

            // Сохраняем файл через ImageStorage (он сам сгенерирует имя)
            String savedFileName = getImageStorage().saveImage(fileImport, originalFileName);
            log.debug("💾 Файл сохранен в хранилище с именем: {}", savedFileName);

            // Устанавливаем имя файла в сущность
            setImageFileName(entity, savedFileName);
            prepareObject(entity, savedFileName);
            log.debug("🔄 Объект подготовлен: {}", entity);

            // Сохраняем сущность в БД
            E savedEntity = getRepository().save(entity);
            log.info("✅ Изображение успешно сохранено!");
            log.info("   Имя файла: {}", savedFileName);
            log.info("   Сущность ID: {}", getEntityId(savedEntity));

            return savedEntity;

        } catch (IOException e) {
            log.error("❌ Ошибка при сохранении изображения: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Неожиданная ошибка: {}", e.getMessage(), e);
            throw new IOException("Ошибка сохранения изображения", e);
        }
    }

    /**
     * Валидация загружаемого файла
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file == null) {
            throw new IOException("Файл не может быть null");
        }

        if (file.isEmpty()) {
            throw new IOException("Файл не может быть пустым");
        }

        // Проверка расширения
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Имя файла не может быть пустым");
        }

        // Проверка на допустимые типы (можно вынести в конфиг)
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new IOException("Файл должен быть изображением");
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