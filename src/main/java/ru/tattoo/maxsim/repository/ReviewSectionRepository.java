package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.ReviewSection;

@Repository
public interface ReviewSectionRepository extends JpaRepository<ReviewSection, Long> {


}
