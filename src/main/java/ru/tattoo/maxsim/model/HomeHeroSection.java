package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "home_hero_section") // Явно указываем имя таблицы
public class HomeHeroSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_name") // Явно указываем имя колонки
    private String imageName;

    @Column(name = "title") // Соответствует новому названию
    private String title;

    @Column(name = "subtitle") // Соответствует новому названию
    private String subtitle;

    @Column(name = "description") // Добавляем поле для textH3
    private String description;

    @Column(name = "section")
    private String section;

    @Column(name = "display_order") // Добавляем порядок отображения
    private Integer displayOrder;

    @Column(name = "is_active") // Добавляем статус активности
    private Boolean isActive;

}