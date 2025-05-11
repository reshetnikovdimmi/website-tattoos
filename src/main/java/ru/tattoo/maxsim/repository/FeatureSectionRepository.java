package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.FeatureSection;

import java.util.Optional;

@Repository
public interface FeatureSectionRepository extends JpaRepository<FeatureSection, Long> {
    @Query("SELECT imageName  FROM FeatureSection WHERE id = ?1")
    Optional<String> getName(Long id);
}
