package ru.tattoo.maxsim.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.tattoo.maxsim.model.Sketches;

public interface SketchesRepository extends JpaRepository<Sketches, Long> {
}
