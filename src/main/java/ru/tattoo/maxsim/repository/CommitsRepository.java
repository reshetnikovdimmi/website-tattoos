package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.Commits;

import java.util.List;

@Repository
public interface CommitsRepository extends JpaRepository<Commits, Long> {
    @Query("SELECT e FROM Commits e ORDER BY ID DESC LIMIT 4")
    List<Commits> findLimit();

}
