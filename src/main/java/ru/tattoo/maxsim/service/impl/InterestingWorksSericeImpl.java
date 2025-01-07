package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.InterestingWorks;
import ru.tattoo.maxsim.repository.InterestingWorksRepository;
import ru.tattoo.maxsim.service.interf.InterestingWorksService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
public class InterestingWorksSericeImpl extends AbstractCRUDService<InterestingWorks, Long> implements InterestingWorksService {

    @Autowired
    private InterestingWorksRepository interestingWorksRepository;

    @Override
    CrudRepository<InterestingWorks, Long> getRepository() {
        return interestingWorksRepository;
    }

    @Override
    public void saveImg(MultipartFile fileImport, String description) throws IOException {
        InterestingWorks interestingWorks = new InterestingWorks();
        interestingWorks.setImageName(fileImport.getOriginalFilename());
        interestingWorks.setDescription(description);
        interestingWorks.setDate(new Date());

        interestingWorksRepository.save(interestingWorks);

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY , fileImport.getOriginalFilename());
        Files.write(fileNameAndPath, fileImport.getBytes());
    }

    @Override
    public void deleteImg(Long id) throws IOException {
        Files.delete(Paths.get(UPLOAD_DIRECTORY, interestingWorksRepository.getName(id)));
        interestingWorksRepository.deleteById(id);
    }
}
