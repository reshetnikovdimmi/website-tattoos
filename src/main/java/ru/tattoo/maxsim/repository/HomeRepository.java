package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.Home;

import java.util.Optional;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {

    @Query("SELECT imageName  FROM Home WHERE id = ?1")
    Optional<String>  getName(Long id);



    String findImageNameById(Long id);
}

