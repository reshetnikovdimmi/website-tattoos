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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommitsServiceImpl extends AbstractCRUDService<Commits, Long> implements CommitsService {

    @Autowired
    private CommitsRepository commitsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    CrudRepository<Commits, Long> getRepository() {
        return commitsRepository;
    }

    @Override
    public void saveCommit(String comment, String name) throws IOException {
        Commits commit = new Commits();
        commit.setComment(comment);
        commit.setUserName(name);
        commit.setDate(new Date());

        getRepository().save(commit);
    }

    @Override
    public List<CommitsDTO> findLimit() {
        return commitsRepository.findLimit()
                .stream()
                .map(commit -> modelMapper.map(commit, CommitsDTO.class))
                .collect(Collectors.toList());
    }
}
