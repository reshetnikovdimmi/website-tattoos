package ru.tattoo.maxsim.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.GalleryDTO;
import ru.tattoo.maxsim.model.DTO.SketchesDTO;
import ru.tattoo.maxsim.model.Sketches;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface SketchesService extends CRUDService<Sketches, Long>{

    void saveImg(MultipartFile fileImport, String descriptiony) throws IOException;

    void deleteImg(Long id) throws IOException;

    Page<Sketches> partition(Pageable p);

    SketchesDTO pageList(String category, Principal principal, int pageSize, int pageNumber);

    List<Sketches> findLimit();
}
