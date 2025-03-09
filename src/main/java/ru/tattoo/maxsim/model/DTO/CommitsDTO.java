package ru.tattoo.maxsim.model.DTO;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@ToString
public class CommitsDTO {
    private Long id;
    private String userName;
    private String comment;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date date;
    private UserDTO user;
}
