package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.ChooseusSection;

import java.util.Optional;

@Repository
public interface ChooseusSectionRepository extends JpaRepository<ChooseusSection, Long> {

    @Query("SELECT imageName  FROM ChooseusSection WHERE id = ?1")
    Optional<String> getName(Long id);
}
