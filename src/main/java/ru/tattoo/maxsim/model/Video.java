package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "home_id")
    private Long homeId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;              // Название видео

    @Column(name = "description", length = 500)
    private String description;        // Описание видео

    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;           // Ссылка на видео (VK, YouTube, etc.)

    @Column(name = "preview_image", length = 255)
    private String previewImage;       // Имя файла превью (обложка)

    @Column(name = "display_order")
    private Integer displayOrder = 0;  // Порядок отображения

    @Column(name = "is_active")
    private Boolean isActive = true;   // Активно ли видео

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
