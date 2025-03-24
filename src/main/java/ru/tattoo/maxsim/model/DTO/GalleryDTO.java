package ru.tattoo.maxsim.model.DTO;


import lombok.*;
import ru.tattoo.maxsim.model.Images;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class GalleryDTO {
    private List<List<Images>> galleryImg;
    private Integer pageSize;
    private Integer currentPage;
    private List<Integer> select;
    private long imagesTotal;
}
