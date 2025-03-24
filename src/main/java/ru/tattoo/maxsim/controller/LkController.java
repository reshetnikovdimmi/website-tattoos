package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tattoo.maxsim.model.DTO.UserDTO;
import ru.tattoo.maxsim.model.Images;
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

        model.addAttribute("UserDTO", modelMapper.map(userRepository.findByLogin(principal.getName()), UserDTO.class));
        model.addAttribute("images", imagesRepository.findByUserName(principal.getName()));

        Page<Images> images = imagesService.partition(principal.getName(), PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize()));

        model.addAttribute("number", PageSize.IMG_9.getPageSize());
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("images1", imagesService.pageList(images));
        model.addAttribute("options", PageSize.getLisPageSize());
        System.out.println(imagesService.pageList(principal));
        model.addAttribute("gallery", imagesService.pageList(principal));
        return "lk";
    }
    @RequestMapping(value = "/sketchesrs/{page}/{number}", method = RequestMethod.GET)
    private String sketchesrsModal(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        Page<Sketches> images = sketchesService.partition(PageRequest.of(page,number));
        model.addAttribute("number", number);
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("images1", sketchesService.pageList(images));
        model.addAttribute("options", PageSize.getLisPageSize());
        return "fragments::modal-img";
    }
    @GetMapping("/user-info")
    public String userInfo(Model model, Principal principal) {
        User user = userRepository.findByLogin(principal.getName()).orElse(null);
        model.addAttribute("review", user.getReviews());
        return "fragments :: review-fragment";
    }

    @PostMapping("/reviews-user-import")
    public String reviewsImport(@RequestParam("imageName") String imageName, @RequestParam("Comment") String Comment, Model model, Principal principal) throws IOException, ParseException  {

        reviewService.saveImd(imageName,Comment,principal.getName());
        User user = userRepository.findByLogin(principal.getName()).orElse(null);

        model.addAttribute("review", user.getReviews());
        return "fragments :: input-user-reviews";
    }

    @PostMapping("/tattoos-user-import")
    public String tattoosImport(@RequestParam("file") MultipartFile fileImport, Model model, Principal principal) throws IOException, ParseException  {

        imagesService.saveImg(fileImport,null,null,principal.getName());

        model.addAttribute("images", imagesRepository.findByUserName(principal.getName()));
        return "fragments :: first-fragment";
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
        if (user.getAvatar()!=null)userService.deleteImg(user.getId());
        userService.saveImg(fileImport, user.getId());
        user = userRepository.findByLogin(principal.getName()).get();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        model.addAttribute("UserDTO", userDTO);
        return "lk::avatar";
    }
}
