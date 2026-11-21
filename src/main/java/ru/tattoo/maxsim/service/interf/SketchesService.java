package ru.tattoo.maxsim.service.interf;


import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Sketches;

import java.security.Principal;
import java.util.List;

public interface SketchesService extends CRUDService<Sketches, Long>{


    PageResult<Sketches> getSketchesPage(int page, int size);
    List<Sketches> findLimit();
}
