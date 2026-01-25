package ru.tattoo.maxsim.service.interf;


import ru.tattoo.maxsim.model.DTO.SketchesDTO;
import ru.tattoo.maxsim.model.Sketches;

import java.security.Principal;
import java.util.List;

public interface SketchesService extends CRUDService<Sketches, Long>{


    SketchesDTO getSketchesDto(String category, Principal principal, int pageSize, int pageNumber);
    List<Sketches> findLimit();
}
