package ru.tattoo.maxsim.service.interf;


import java.io.IOException;
import java.util.List;

public interface CRUDService<E, K> {

    void create(E entity);

    E findById(K id);

    List<E> findAll();

    E update(E entity);

    void delete(E entity);

    void deleteById(K id) throws IOException;

    void deleteAll();

    List<E> saveAll(List<E> l);




}
