package ru.tattoo.maxsim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.UserRepository;


import java.util.List;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    public List<User> listShop() {

        return (List<User>) userRepository.findAll();
    }


}
