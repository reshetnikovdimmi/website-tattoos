package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class ClassesSection{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_name")
    private String imageName;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Lob
    @Column(name = "details")
    private String details;
    @Column(name = "category_title")
    private String categoryTitle;
    @Column(name = "section")
    private String section;
}
