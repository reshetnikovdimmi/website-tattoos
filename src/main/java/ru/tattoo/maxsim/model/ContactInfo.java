package ru.tattoo.maxsim.model;

import jakarta.persistence.Entity;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@ToString
public class ContactInfo {
    private String tell;
    private String email;
    private String address;
}
