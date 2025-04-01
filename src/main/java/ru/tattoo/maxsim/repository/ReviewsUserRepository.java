package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.ReviewsUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewsUserRepository extends JpaRepository<ReviewsUser, Long> {

    @Query("SELECT e FROM ReviewsUser e ORDER BY ID DESC LIMIT 4")
    List<ReviewsUser> findLimit();

    @Query("SELECT imageName  FROM ReviewsUser WHERE id = ?1")
    Optional<String> findNameById(Long id);

    @Query("SELECT COUNT(id) FROM ReviewsUser")
    int getCount();


}
