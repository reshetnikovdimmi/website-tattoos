package ru.tattoo.maxsim.service.interf;

import ru.tattoo.maxsim.model.SiteVerification;

import java.util.List;
import java.util.Optional;

public interface SiteVerificationService {

    List<SiteVerification> getAll();

    List<SiteVerification> getByEngine(SiteVerification.SearchEngine engine);

    Optional<SiteVerification> getActiveMetaTag(SiteVerification.SearchEngine engine);

    SiteVerification save(SiteVerification verification);

    void delete(Long id);

    Optional<SiteVerification> findById(Long id);

    String generateHtmlFileContent(String verificationCode, String engine);
}
