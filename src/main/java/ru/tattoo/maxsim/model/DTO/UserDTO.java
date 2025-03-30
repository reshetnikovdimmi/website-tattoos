package ru.tattoo.maxsim.model.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.ReviewsUser;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class UserDTO {
    private Long id;
    private String login;
    private String email;
    private String avatar;
    private Date date;
    private List<ReviewsUser> reviews;
    private List<Images> images;
}
