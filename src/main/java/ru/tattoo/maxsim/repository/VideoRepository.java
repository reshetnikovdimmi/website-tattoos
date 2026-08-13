package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.Video;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // Все активные видео, отсортированные по порядку
    @Query("SELECT v FROM Video v WHERE v.isActive = true ORDER BY v.displayOrder ASC, v.createdAt DESC")
    List<Video> findAllActive();

    // Все видео, отсортированные по порядку
    @Query("SELECT v FROM Video v ORDER BY v.displayOrder ASC, v.createdAt DESC")
    List<Video> findAllOrdered();

    // Поиск по названию
    List<Video> findByTitleContainingIgnoreCase(String title);
}