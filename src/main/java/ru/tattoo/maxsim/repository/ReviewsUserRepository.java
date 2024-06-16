package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.ReviewsUser;

import java.util.List;

@Repository
public interface ReviewsUserRepository extends JpaRepository<ReviewsUser, Long> {
    @Query("SELECT e FROM ReviewsUser e ORDER BY ID DESC LIMIT 4")
    List<ReviewsUser> findLimit();
}