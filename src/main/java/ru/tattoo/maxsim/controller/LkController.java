package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.ReviewsUser;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.model.User;
import ru.tattoo.maxsim.repository.ImagesRepository;
import ru.tattoo.maxsim.repository.UserRepository;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.service.interf.ReviewService;
import ru.tattoo.maxsim.service.interf.SketchesService;
import ru.tattoo.maxsim.service.interf.UserService;
import ru.tattoo.maxsim.util.PageSize;

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
    private ReviewService reviewService;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private SketchesService sketchesService;

    @Autowired
    private ModelMapper modelMapper;

    private static final int PAGE_NUMBER = 0;

    @GetMapping("/lk")
    public String login(Model model, Principal principal) {
        User user = userRepository.findByLogin(principal.getName()).orElse(null);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        model.addAttribute("UserDTO", userDTO);
        model.addAttribute("images", imagesRepository.findByUserName(principal.getName()));

        Page<Sketches> images = sketchesService.partition(PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize()));
        model.addAttribute("number", PageSize.IMG_9.getPageSize());
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("images1", sketchesService.pageList(images));
        model.addAttribute("options", PageSize.getLisPageSize());
        return "lk";
    }
    @GetMapping("/user-info")
    public String userInfo(Model model, Principal principal) {
        User user = userRepository.findByLogin(principal.getName()).orElse(null);
        model.addAttribute("review", user.getReviews());
        return "fragments :: review-fragment";
    }

    @PostMapping("/reviews-user-import")
    public String reviewsImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("Comment") String Comment, Model model, Principal principal) throws IOException, ParseException  {
        reviewService.saveImd(fileImport,Comment,principal.getName());
        User user = userRepository.findByLogin(principal.getName()).orElse(null);

        model.addAttribute("review", user.getReviews());
        return "fragments :: input-user-reviews";
    }

    @PostMapping("/tattoos-user-import")
    public String tattoosImport(@RequestParam("file") MultipartFile fileImport, Model model, Principal principal) throws IOException, ParseException  {

        imagesService.saveImg(fileImport,null,null,principal.getName());

        model.addAttribute("images", imagesRepository.findByUserName(principal.getName()));
        return "fragments :: tattoos-user-reviews";
    }


    @GetMapping("/profile-editing")
    public String profileEditing(Model model, Principal principal) {
        User user = userRepository.findByLogin(principal.getName()).get();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        model.addAttribute("UserDTO", userDTO);
        return "fragments :: profile-editing";
    }

    @GetMapping("/user-tattoos")
    public String userTattoos(Model model, Principal principal) {
      model.addAttribute("images", imagesRepository.findByUserName(principal.getName()));
        return "fragments :: first-fragment";
    }

    @PostMapping("/avatar-import")
    public String avatarImport(@RequestParam("file") MultipartFile fileImport, Model model, Principal principal) throws IOException, ParseException {

        User user = userRepository.findByLogin(principal.getName()).orElse(null);
        userService.deleteImg(user.getId());
        userService.saveImg(fileImport, user.getId());
        user = userRepository.findByLogin(principal.getName()).get();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        model.addAttribute("UserDTO", userDTO);
        return "lk::avatar";
    }
}
