package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.InterestingWorks;
import java.util.List;

@Repository
public interface InterestingWorksRepository extends JpaRepository<InterestingWorks, Long> {

    @Query("SELECT e FROM InterestingWorks e ORDER BY ID DESC LIMIT 4")
    List<InterestingWorks> findLimit();

    @Query("SELECT imageName  FROM InterestingWorks WHERE id = ?1")
    String getName(Long id);
}
