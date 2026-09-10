package ru.tattoo.maxsim.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tattoo.maxsim.model.SiteVerification;
import ru.tattoo.maxsim.repository.SiteVerificationRepository;
import ru.tattoo.maxsim.service.interf.SiteVerificationService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteVerificationServiceImpl implements SiteVerificationService {

    private final SiteVerificationRepository repository;

    @Override
    public List<SiteVerification> getAll() {
        return repository.findAll();
    }

    @Override
    public List<SiteVerification> getByEngine(SiteVerification.SearchEngine engine) {
        return repository.findByEngine(engine);
    }

    @Override
    public Optional<SiteVerification> getActiveMetaTag(SiteVerification.SearchEngine engine) {
        return repository.findByEngineAndMethodAndActiveTrue(engine, SiteVerification.VerificationMethod.META_TAG);
    }

    @Override
    @Transactional
    public SiteVerification save(SiteVerification verification) {
        // Деактивируем ВСЕ активные записи этого движка/метода
        List<SiteVerification> actives = repository
                .findAllByEngineAndMethodAndActiveTrue(
                        verification.getEngine(), verification.getMethod());

        for (SiteVerification existing : actives) {
            // Не трогаем саму себя при обновлении
            if (existing.getId() != null && existing.getId().equals(verification.getId())) {
                continue;
            }
            existing.setActive(false);
            repository.save(existing);
            log.info("Деактивирована старая верификация: {} / {} (id={})",
                    existing.getEngine(), existing.getMethod(), existing.getId());
        }

        verification.setActive(true);
        SiteVerification saved = repository.save(verification);
        log.info("Сохранена верификация: {} / {} (id={})",
                saved.getEngine(), saved.getMethod(), saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
        log.info("Удалена верификация id={}", id);
    }

    @Override
    public Optional<SiteVerification> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public String generateHtmlFileContent(String verificationCode, String engine) {
        if (engine.equalsIgnoreCase("YANDEX")) {
            return "<html>\n" +
                    "<head>\n" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                    "</head>\n" +
                    "<body>Verification: " + verificationCode + "</body>\n" +
                    "</html>";
        }
        // Google ждёт "google-site-verification: {filename}"
        return "google-site-verification: google" + verificationCode + ".html";
    }
}
