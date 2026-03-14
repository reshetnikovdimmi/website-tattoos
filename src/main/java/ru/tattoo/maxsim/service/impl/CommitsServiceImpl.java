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
import ru.tattoo.maxsim.storage.ImageStorage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommitsServiceImpl extends AbstractCRUDService<Commits, Long> implements CommitsService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CommitsRepository commitsRepository;

    @Autowired
    private ImageStorage imageStorage;

    @Override
    protected ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    protected String getImageFileName(Commits entity) {
        return entity != null ? entity.getUserName() : null;
    }

    @Override
    protected void setImageFileName(Commits entity, String fileName) {
        if (entity != null) {
            entity.setUserName(fileName);
        }
    }

    @Override
    void prepareObject(Commits entity, String fileName) {
        setImageFileName(entity, fileName);
    }

    @Override
    CrudRepository<Commits, Long> getRepository() {
        return commitsRepository;
    }

    @Override
    public List<CommitsDTO> findLimit() {
        return commitsRepository.findLimit()
                .stream()
                .map(commit -> modelMapper.map(commit, CommitsDTO.class))
                .collect(Collectors.toList());
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
}