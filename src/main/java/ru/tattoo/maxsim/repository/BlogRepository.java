package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.Blog;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> findTop4ByDescriptionIsNotNullOrderByDateDesc();

    List<Blog> findByDescriptionIsNotNullOrderByDateDesc();

    @Query("SELECT imageName  FROM Blog WHERE id = ?1")
    Optional<String> findNameById(Long id);
}
