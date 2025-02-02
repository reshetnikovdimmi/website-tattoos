package ru.tattoo.maxsim.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Size(min = 2, max = 20, message = "Login should be from 2 to 20 chars")
    private String login;
    private String password;
    private String role;
    private String email;
    private String avatar;

}
