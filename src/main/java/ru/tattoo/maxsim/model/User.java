package ru.tattoo.maxsim.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;


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
    @Column(unique=true)
    private String login;
    private String password;
    private String role;
    @Column(unique=true)
    private String email;
    private String avatar;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date date;
    @Transient
    private Integer pagesPerSheet;

    @OneToMany(targetEntity = ReviewsUser.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "userName", referencedColumnName = "login")

    private List<ReviewsUser> reviews;

    @OneToMany(targetEntity = Images.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "userName", referencedColumnName = "login")

    private List<Images> images;
}
