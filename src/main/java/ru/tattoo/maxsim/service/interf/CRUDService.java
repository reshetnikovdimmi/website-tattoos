package ru.tattoo.maxsim.service.interf;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CRUDService<E, K> {

    void create(E entity);

    E findById(K id);

    List<E> findAll();

    E update(E entity);

    void delete(E entity);

    void deleteAll();

    List<E> saveAll(List<E> l);

    void saveImg(MultipartFile fileImport, E entity) throws IOException;

    void deleteById(K id) throws IOException;



}
