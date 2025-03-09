package ru.tattoo.maxsim.service.impl;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.Commits;
import ru.tattoo.maxsim.model.DTO.CommitsDTO;
import ru.tattoo.maxsim.repository.CommitsRepository;
import ru.tattoo.maxsim.service.interf.CommitsService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class CommitsServiceImpl extends AbstractCRUDService<Commits,Long> implements CommitsService {

    @Autowired
    private CommitsRepository commitsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    CrudRepository<Commits, Long> getRepository() {
        return commitsRepository;
    }

    @Override
    public void saveImd(String comment, String name) throws IOException {

        Commits commits = new Commits();
        commits.setComment(comment);
        commits.setUserName(name);
        commits.setDate(new Date());

        commitsRepository.save(commits);

    }


}
