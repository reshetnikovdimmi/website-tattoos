package ru.tattoo.maxsim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.service.interf.ContactInfoService;
import ru.tattoo.maxsim.storage.ImageStorage;

@Service
public class ContactInfoServiceImpl extends AbstractCRUDService<ContactInfo, Long> implements ContactInfoService {

  @Autowired
  private ContactInfoRepository contactInfoRepository;

  @Autowired
  private ImageStorage imageStorage;

  @Override
  protected ImageStorage getImageStorage() {
    return imageStorage;
  }

  @Override
  protected String getImageFileName(ContactInfo entity) {
    return null;
  }

  @Override
  protected void setImageFileName(ContactInfo entity, String fileName) {

  }

  @Override
  void prepareObject(ContactInfo entity, String fileName) {
    setImageFileName(entity, fileName);
  }

  @Override
  CrudRepository<ContactInfo, Long> getRepository() {
    return contactInfoRepository;
  }
}