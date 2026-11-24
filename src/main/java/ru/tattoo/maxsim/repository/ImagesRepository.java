package ru.tattoo.maxsim.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.Images;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {

    Page<Images> findByCategory(String category, Pageable pageable);

    Page<Images> findAllByUserName(String userName, Pageable pageable);

    Page<Images> findByCategoryAndUserName(String category, String userName, Pageable pageable);

    List<Images> findByFlagTrue();
}
