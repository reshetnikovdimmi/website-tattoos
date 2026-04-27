package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class PriceSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_name")
    private String imageName;
    @Column(name = "plan_name")
    private String planName;
    @Column(name = "price")
    private String price;
    @Lob
    @Column(name = "description")
    private String description;
    @Column(name = "feature_1")
    private String feature1;
    @Column(name = "feature_2")
    private String feature2;
    @Column(name = "category_title")
    private String categoryTitle;
    @Column(name = "section")
    private String section;
}
