package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.ClassesSection;

@Repository
public interface ClassesSectionRepository extends JpaRepository<ClassesSection, Long> {
}
