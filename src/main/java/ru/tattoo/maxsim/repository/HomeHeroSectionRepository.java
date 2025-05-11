package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.HomeHeroSection;

import java.util.Optional;

@Repository
public interface HomeHeroSectionRepository extends JpaRepository<HomeHeroSection, Long> {
    @Query("SELECT imageName  FROM HomeHeroSection WHERE id = ?1")
    Optional<String> getName(Long id);
}
