package ru.tattoo.maxsim.model;


import jakarta.persistence.*;
import jdk.jfr.BooleanFlag;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Column(unique=true)
    private String imageName;
    private String category;
    private String description;
    private String userName;
    @BooleanFlag
    private boolean flag;
}
