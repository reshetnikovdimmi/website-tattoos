package ru.tattoo.maxsim.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Sketches;
import java.io.IOException;

public interface SketchesService extends CRUDService<Sketches, Long>{

    void saveImg(MultipartFile fileImport, String descriptiony) throws IOException;

    void deleteImg(Long id) throws IOException;

    Page<Sketches> partition(Pageable p);

    Object pageList(Page<Sketches> images);
}
