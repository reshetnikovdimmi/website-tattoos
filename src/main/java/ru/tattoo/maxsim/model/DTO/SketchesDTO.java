package ru.tattoo.maxsim.model.DTO;


import lombok.*;
import ru.tattoo.maxsim.model.Sketches;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SketchesDTO {
    private List<List<Sketches>> sketchesImg;
    private Integer pageSize;
    private Integer currentPage;
    private Integer number;
    private List<Integer> select;
    private long imagesTotal;
}