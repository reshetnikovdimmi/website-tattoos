package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.InterestingWorks;
import ru.tattoo.maxsim.repository.InterestingWorksRepository;
import ru.tattoo.maxsim.service.interf.InterestingWorksService;
import ru.tattoo.maxsim.util.ImageUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InterestingWorksSericeImpl extends AbstractCRUDService<InterestingWorks, Long> implements InterestingWorksService {

    @Autowired
    private InterestingWorksRepository interestingWorksRepository;

    @Override
    CrudRepository<InterestingWorks, Long> getRepository() {
        return interestingWorksRepository;
    }

    @Override
    public void saveInterestingWorks(MultipartFile fileImport, String description) throws IOException {
        InterestingWorks interestingWorks = new InterestingWorks();
        interestingWorks.setImageName(ImageUtils.generateUniqueFileName(fileImport.getOriginalFilename()));
        Optional.ofNullable(description).ifPresent(interestingWorks::setDescription);
        interestingWorks.setDate(new Date());

        interestingWorksRepository.save(interestingWorks);

        ImageUtils.saveImage(fileImport, interestingWorks.getImageName());

    }

    @Override
    public void deleteInterestingWorks(Long id) throws IOException {

        Optional<String> imageName = interestingWorksRepository.findNameById(id);
        imageName.ifPresent(name -> {
            try {
                ImageUtils.deleteImage(name);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка удаления файла", e);
            }
        });
        interestingWorksRepository.deleteById(id);
    }

    @Override
    public List<InterestingWorks> findLimit() {
        return interestingWorksRepository.findTop4ByOrderByIdDesc();
    }
}
