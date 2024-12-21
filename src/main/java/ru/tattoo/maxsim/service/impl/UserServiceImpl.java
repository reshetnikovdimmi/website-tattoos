package ru.tattoo.maxsim.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.interf.UserService;
import java.util.List;

@Service
public class UserServiceImpl extends AbstractCRUDService<User, Long> implements UserService{
@Autowired
private UserRepository userRepository;

    @Override
    CrudRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }
}
