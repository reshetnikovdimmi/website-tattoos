package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.PriceSection;
import ru.tattoo.maxsim.repository.PriceSectionRepository;
import ru.tattoo.maxsim.service.interf.PriceSectionService;


@Service
public class PriceSectionServiceImpl extends AbstractCRUDService<PriceSection, Long> implements PriceSectionService {

    @Autowired
    private PriceSectionRepository priceSectionRepository;


    @Override
    CrudRepository<PriceSection, Long> getRepository() {
        return priceSectionRepository;
    }
}
