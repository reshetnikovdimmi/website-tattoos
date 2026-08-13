package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "review_section")
public class ReviewSection {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "home_id")
    private Long homeId;

    @Column(name = "section_type")
    private String sectionType; // "all-reviews" или "instagram"

    @Column(name = "image_name")
    private String imageName;

    private String title;

    @Lob
    private String description;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "button_text")
    private String buttonText;
}
