package ru.tattoo.maxsim.service.impl;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    void prepareObject(Commits entity, String s) {

    }

    @Override
    CrudRepository<Commits, Long> getRepository() {
        return commitsRepository;
    }

    @Override
    public void create(Commits entity) {

        String username = getCurrentUsername();
        entity.setUserName(username);
        entity.setDate(new Date());

        getRepository().save(entity);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public List<CommitsDTO> findLimit() {
        return commitsRepository.findLimit()
                .stream()
                .map(commit -> modelMapper.map(commit, CommitsDTO.class))
                .collect(Collectors.toList());
    }
}
