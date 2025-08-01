package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class ChooseusSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageName;
    private String textH1;
    @Lob
    private String textH2;
    @Lob
    private String textH3;
    private String textH4;
    private String textH5;

    private String title;

    private String section;
}
