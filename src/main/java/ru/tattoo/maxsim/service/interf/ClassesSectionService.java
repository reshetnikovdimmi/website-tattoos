package ru.tattoo.maxsim.service.interf;

import ru.tattoo.maxsim.model.ClassesSection;

import java.util.List;

public interface ClassesSectionService extends CRUDService<ClassesSection, Long> {
   List<ClassesSection> findByTitle(String s);
}
