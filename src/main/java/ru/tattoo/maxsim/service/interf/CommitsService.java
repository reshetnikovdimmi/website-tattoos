package ru.tattoo.maxsim.service.interf;


import ru.tattoo.maxsim.model.Commits;
import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.model.DTO.CommitsDTO;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface CommitsService extends CRUDService<Commits,Long>{
    void saveImd(String comment, String name) throws IOException;

    List<CommitsDTO> findLimit();
}
