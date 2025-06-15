package ru.tattoo.maxsim.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.PriceSection;

@Repository
public interface PriceSectionRepository extends JpaRepository<PriceSection, Long> {
}
