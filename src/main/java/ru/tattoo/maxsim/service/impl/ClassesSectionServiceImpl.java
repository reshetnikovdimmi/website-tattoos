package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.ClassesSection;
import ru.tattoo.maxsim.repository.ClassesSectionRepository;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;

@Service
public class ClassesSectionServiceImpl extends AbstractCRUDService<ClassesSection, Long> implements ClassesSectionService {

    @Autowired
    private ClassesSectionRepository classesSectionRepository;

    @Override
    CrudRepository<ClassesSection, Long> getRepository() {
        return classesSectionRepository;
    }
}
