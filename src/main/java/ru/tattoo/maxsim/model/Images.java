package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images", indexes = {
        @Index(name = "idx_images_category", columnList = "category"),
        @Index(name = "idx_images_user_name", columnList = "userName"),
        @Index(name = "idx_images_flag", columnList = "flag")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String imageName;

    private String category;
    private String description;

    private String userName;

    private boolean flag;
}
