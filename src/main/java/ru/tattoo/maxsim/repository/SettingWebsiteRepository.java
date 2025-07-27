package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tattoo.maxsim.model.SettingWebsite;

@Repository
public interface SettingWebsiteRepository extends JpaRepository<SettingWebsite, Long> {
}
