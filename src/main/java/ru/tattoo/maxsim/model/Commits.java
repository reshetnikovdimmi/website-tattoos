package ru.tattoo.maxsim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class Commits {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String userName;
    private String comment;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date date;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "userName", referencedColumnName = "login", insertable = false, updatable = false,  foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))

    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;
}
