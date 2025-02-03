package ru.tattoo.maxsim.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.UserService;

import java.security.Principal;

@Controller
public class LkController {

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/lk")
    public String login(Model model, Principal principal) {
        User user = userRepository.findByLogin(principal.getName()).get();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        model.addAttribute("UserDTO", userDTO);
        model.addAttribute("images", imagesService.findAll());
        return "lk";
    }
    @GetMapping("/user-info")
    public String userInfo(Model model) {
        return "fragments :: second-fragment";
    }


    @GetMapping("/profile-editing")
    public String profileEditing(Model model, Principal principal) {
        User user = userRepository.findByLogin(principal.getName()).get();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        model.addAttribute("UserDTO", userDTO);
        return "fragments :: profile-editing";
    }

    @GetMapping("/user-tattoos")
    public String userTattoos(Model model) {
        model.addAttribute("images", imagesService.findAll());
        return "fragments :: first-fragment";
    }
}
