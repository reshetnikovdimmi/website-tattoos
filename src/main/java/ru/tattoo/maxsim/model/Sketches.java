package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class Sketches {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String imageName;
    private String description;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date date;
}
