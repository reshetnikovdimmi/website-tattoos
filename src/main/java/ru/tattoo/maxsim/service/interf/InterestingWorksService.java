package ru.tattoo.maxsim.service.interf;

import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.InterestingWorks;
import java.io.IOException;
import java.util.List;

public interface InterestingWorksService extends CRUDService<InterestingWorks, Long>{

    void saveInterestingWorks(MultipartFile fileImport, String description) throws IOException;

    void deleteInterestingWorks(Long id) throws IOException;

    List<InterestingWorks> findLimit();
}
