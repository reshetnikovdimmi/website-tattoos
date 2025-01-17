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
public class ContactInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String tell;
    private String email;
    private String address;
}
