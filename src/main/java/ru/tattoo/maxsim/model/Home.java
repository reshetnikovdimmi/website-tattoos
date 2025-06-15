package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class Home {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String title; // Заголовок главной страницы
    private String introText; // Приветственный текст
    private String metaDescription; // Meta-информация для поисковиков

    private String imageName;
    private String section;
    private String textH1;
    private String textH2;
    private String textH3;


    @OneToMany(targetEntity = HomeHeroSection.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "section", referencedColumnName = "section")
    private List<HomeHeroSection> banners;

    @OneToMany(targetEntity = FeatureSection.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "section", referencedColumnName = "section")
    private List<FeatureSection> feature;

    @OneToMany(targetEntity = AboutSection.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "section", referencedColumnName = "section")
    private List<AboutSection> about;

    @OneToMany(targetEntity = ClassesSection.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "section", referencedColumnName = "section")
    private List<ClassesSection> classes;

    @OneToMany(targetEntity = PriceSection.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "section", referencedColumnName = "section")
    private List<PriceSection> price;
}

