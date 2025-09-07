package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.service.interf.ContactInfoService;

@Service
public class ContactInfoServiceImpl extends AbstractCRUDService<ContactInfo, Long> implements ContactInfoService {
  @Autowired
  private ContactInfoRepository contactInfoRepository;


    @Override
    CrudRepository<ContactInfo, Long> getRepository() {
        return contactInfoRepository;
    }
}
