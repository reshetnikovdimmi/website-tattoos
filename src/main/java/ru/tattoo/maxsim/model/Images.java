package ru.tattoo.maxsim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
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
    private String imageName;
    private String category;
    private String description;
    @BooleanFlag
    private boolean flag;
}
