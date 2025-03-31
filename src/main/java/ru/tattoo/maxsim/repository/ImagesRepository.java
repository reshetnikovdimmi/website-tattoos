package ru.tattoo.maxsim.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.Images;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {

    Page<Images> findByCategory(String c, Pageable p);

    Page<Images> findByUserName(String c, Pageable p);

    @Query("SELECT imageName  FROM Images WHERE id = ?1")
    String findNameById(Long id);

    @Query("SELECT c FROM Images c WHERE c.flag = true")
    Iterable<Images> findByFlagTrue();

    List<Images> findByUserName(String s);


}
