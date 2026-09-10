package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.SiteVerification;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteVerificationRepository extends JpaRepository<SiteVerification, Long> {

    List<SiteVerification> findByEngine(SiteVerification.SearchEngine engine);

    Optional<SiteVerification> findByEngineAndMethodAndActiveTrue(
            SiteVerification.SearchEngine engine,
            SiteVerification.VerificationMethod method
    );

    // SiteVerificationRepository.java
    List<SiteVerification> findAllByEngineAndMethodAndActiveTrue(
            SiteVerification.SearchEngine engine, SiteVerification.VerificationMethod method);

    List<SiteVerification> findByActiveTrue();

    boolean existsByEngineAndMethod(SiteVerification.SearchEngine engine, SiteVerification.VerificationMethod method);
}