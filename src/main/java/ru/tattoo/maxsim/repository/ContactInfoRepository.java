package ru.tattoo.maxsim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tattoo.maxsim.model.ContactInfo;

import java.util.List;

public interface ContactInfoRepository extends JpaRepository <ContactInfo, Long> {
    @Query("SELECT e FROM ContactInfo e ORDER BY ID DESC LIMIT 1")
    ContactInfo findLimit();
}
