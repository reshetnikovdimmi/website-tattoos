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

    //todo: может использовать в полной мере JPA? Имею ввиду, что простые query можно задавать именем метода и Spring сам построить query
    @Query("SELECT imageName  FROM ReviewsUser WHERE id = ?1")
    String getImageNameById(Long id);

    @Query("SELECT COUNT(id) FROM ReviewsUser")
    int getCount();
}
