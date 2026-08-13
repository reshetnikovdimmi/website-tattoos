package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    private String section;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "home_id", referencedColumnName = "id")
    @OrderBy("displayOrder DESC")
    private List<HomeHeroSection> banners = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "home_id", referencedColumnName = "id")
    private List<FeatureSection> feature = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "home_id", referencedColumnName = "id")
    private List<AboutSection> about = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "home_id", referencedColumnName = "id")
    private List<ClassesSection> classes = new ArrayList<>();

    @OneToMany(targetEntity = PriceSection.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "section", referencedColumnName = "section")
    private List<PriceSection> price;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "home_id", referencedColumnName = "id")
    private List<ChooseusSection> chooseus = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "home_id", referencedColumnName = "id")
    private List<Video> videos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "home_id", referencedColumnName = "id")
    private List<ReviewSection> review = new ArrayList<>();
}

