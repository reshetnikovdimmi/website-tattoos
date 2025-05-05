package ru.tattoo.maxsim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


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
    private String imageName;
    private String category;
    private String textH1;
    private String textH2;
    private String textH3;
}
