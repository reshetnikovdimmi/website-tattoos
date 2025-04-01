package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.InterestingWorks;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterestingWorksRepository extends JpaRepository<InterestingWorks, Long> {

    List<InterestingWorks> findTop4ByOrderByIdDesc();

    @Query("SELECT imageName  FROM InterestingWorks WHERE id = ?1")
    Optional<String> findNameById(Long id);
}
