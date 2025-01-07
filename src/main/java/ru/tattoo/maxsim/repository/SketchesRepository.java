package ru.tattoo.maxsim.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.model.Sketches;

import java.util.List;

public interface SketchesRepository extends JpaRepository<Sketches, Long> {

    @Query("SELECT imageName  FROM Sketches WHERE id = ?1")
    String getName(Long id);

    @Query("SELECT e FROM Sketches e ORDER BY ID DESC LIMIT 3")
    List<Sketches> findLimit();

}
