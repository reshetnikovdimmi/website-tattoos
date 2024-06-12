package ru.tattoo.maxsim.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.tattoo.maxsim.model.Images;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {
    Page<Images> findByCategory(String c, Pageable p);

    @Query("SELECT imageName  FROM Images WHERE id = ?1")
    String getName(Long id);
    @Modifying
    @Transactional
    @Query("DELETE FROM Images WHERE id = ?1")
    List<Images> deleteId(Long id);
}
