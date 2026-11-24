package ru.tattoo.maxsim.service.interf;

import ru.tattoo.maxsim.model.Images;

/**
 * Изменение галереи: CRUD/загрузка и флаг «лучшее».
 */
public interface ImagesCommandService extends CRUDService<Images, Long> {

    void updateImageFlag(Long id, boolean flag);
}
