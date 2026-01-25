package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.repository.PriceSectionRepository;
import ru.tattoo.maxsim.service.interf.PriceSectionService;

import java.util.Optional;


@Service
public class PriceSectionServiceImpl extends AbstractCRUDService<PriceSection, Long> implements PriceSectionService {

    @Autowired
    private PriceSectionRepository priceSectionRepository;


    @Override
    void prepareObject(PriceSection entity, String s) {

    }
    @Override
    public void create(PriceSection entity) {

        Optional<PriceSection> object = getRepository().findById(entity.getId());

        entity.setSection(object.get().getSection());
        entity.setTitle(object.get().getTitle());

        getRepository().save(entity);
    }
    @Override
    CrudRepository<PriceSection, Long> getRepository() {
        return priceSectionRepository;
    }

}
