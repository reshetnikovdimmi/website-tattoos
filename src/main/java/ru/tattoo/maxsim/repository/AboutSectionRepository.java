package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.AboutSection;
import java.util.Optional;


@Repository
public interface AboutSectionRepository extends JpaRepository<AboutSection,Long> {
    @Query("SELECT imageName  FROM AboutSection WHERE id = ?1")
    Optional<String> getName(Long id);
}
