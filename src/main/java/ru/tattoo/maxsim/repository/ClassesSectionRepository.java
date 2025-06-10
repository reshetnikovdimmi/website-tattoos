package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.ClassesSection;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassesSectionRepository extends JpaRepository<ClassesSection, Long> {

    @Query("SELECT imageName  FROM ClassesSection WHERE id = ?1")
    Optional<String> getName(Long id);


    List<ClassesSection> findByTitle(String s);
}
