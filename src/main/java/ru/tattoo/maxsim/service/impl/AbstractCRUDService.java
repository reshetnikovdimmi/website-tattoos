package ru.tattoo.maxsim.service.impl;


import org.checkerframework.checker.units.qual.K;
import org.springframework.data.repository.CrudRepository;
import ru.tattoo.maxsim.service.interf.CRUDService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


public abstract class AbstractCRUDService<E, K> implements CRUDService<E, K>{

    public static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/img/images/";

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
        List<E> o = (List<E>) getRepository().findAll();
        Collections.reverse(o);
        return o;
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



}
