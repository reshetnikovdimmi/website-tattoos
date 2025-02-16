package ru.tattoo.maxsim.service.interf;


import ru.tattoo.maxsim.model.Commits;
import java.io.IOException;

public interface CommitsService extends CRUDService<Commits,Long>{
    void saveImd(String comment, String name) throws IOException;
}
