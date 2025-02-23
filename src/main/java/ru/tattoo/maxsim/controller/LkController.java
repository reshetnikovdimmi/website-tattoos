package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.UserService;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;

@Controller
public class LkController {

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/lk")
    public String login(Model model, Principal principal) {
        User user = userRepository.findByLogin(principal.getName()).orElse(null);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        model.addAttribute("UserDTO", userDTO);
        model.addAttribute("images", imagesService.findAll());
        return "lk";
    }
    @GetMapping("/user-info")
    public String userInfo(Model model) {
        return "fragments :: review-fragment";
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

    @PostMapping("/avatar-import")
    public String avatarImport(@RequestParam("file") MultipartFile fileImport, Model model, Principal principal) throws IOException, ParseException {
        System.out.println(fileImport.getOriginalFilename());
        User user = userRepository.findByLogin(principal.getName()).orElse(null);
        userService.saveImg(fileImport, user.getId());
        user = userRepository.findByLogin(principal.getName()).get();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        model.addAttribute("UserDTO", userDTO);
        return "lk::avatar";
    }
}
