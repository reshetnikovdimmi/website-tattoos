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
public class SettingWebsite {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String imageName;
    private String section;
    private String textH1;
    @Lob
    private String textH2;
}
