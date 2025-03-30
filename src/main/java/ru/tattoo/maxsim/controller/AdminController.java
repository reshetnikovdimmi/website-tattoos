package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.model.DTO.CommitsDTO;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.service.interf.*;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.text.ParseException;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String ALL_GALLERY = "Вся галерея";
    private static final int PAGE_NUMBER = 0;

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private SketchesService sketchesService;

    @Autowired
    private InterestingWorksService interestingWorksService;

    @Autowired
    private CommitsService commitsService;

    @Autowired
    private HomeService homeService;

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping
    public String adminDashboard(Model model) {
        populateAdminDashboard(model);
        return "admin";
    }

    @PostMapping("/img-import")
    public String uploadGalleryImage(@RequestParam("file") MultipartFile fileImport,
                                     @RequestParam("description") String description,
                                     @RequestParam("category") String category,
                                     Model model) throws IOException, ParseException {
        imagesService.saveImg(fileImport, description, category, null);
        updateGallery(model);
        return "admin::img-import";
    }

    @PostMapping("/sketches-import")
    public String uploadSketch(@RequestParam("file") MultipartFile fileImport,
                               @RequestParam("description") String description,
                               Model model) throws IOException, ParseException {
        sketchesService.saveImg(fileImport, description);
        updateSketches(model);
        return "admin::sketches-import";
    }

    @PostMapping("/carousel-import")
    public String uploadCarouselImage(@RequestParam("file") MultipartFile fileImport,
                                      Model model) throws IOException, ParseException {
        homeService.saveImg(fileImport, "carousel");
        updateCarousel(model);
        return "admin::carousel-import";
    }

    @GetMapping("/carousel-delete/{id}")
    public String deleteCarouselImage(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        homeService.deleteImg(id);
        updateCarousel(model);
        return "admin::carousel-import";
    }

    @PostMapping("/interesting-works-import")
    public String uploadInterestingWork(@RequestParam("file") MultipartFile fileImport,
                                        @RequestParam("description") String description,
                                        Model model) throws IOException, ParseException {
        interestingWorksService.saveImg(fileImport, description);
        updateInterestingWorks(model);
        return "admin::interesting-works-import";
    }

    @GetMapping("/interesting-works-delete/{id}")
    public String deleteInterestingWork(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        interestingWorksService.deleteImg(id);
        updateInterestingWorks(model);
        return "admin::interesting-works-import";
    }

    @GetMapping("/sketches-delete/{id}")
    public String deleteSketch(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        sketchesService.deleteImg(id);
        updateSketches(model);
        return "admin::sketches-import";
    }

    @GetMapping("/img-delete/{id}")
    public String deleteGalleryImage(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        imagesService.deleteImg(id);
        updateGallery(model);
        return "admin::img-import";
    }

    @GetMapping("/{style}/{page}/{number}")
    public String searchGallery(@PathVariable("style") String style,
                                @PathVariable("page") int page,
                                @PathVariable("number") int number,
                                Model model) {
        model.addAttribute("gallery", imagesService.pageList(style.equals(ALL_GALLERY) ? null : style, null, number, page));
        return "admin::img-import";
    }

    @GetMapping("/sketches/{page}/{number}")
    public String searchSketches(@PathVariable("page") int page,
                                 @PathVariable("number") int number,
                                 Model model) {
        model.addAttribute("sketches", sketchesService.pageList(null, null, number, page));
        return "admin::sketches-import";
    }

    @GetMapping("/reviews-delete/{id}")
    public String deleteReview(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        reviewService.deleteImg(id);
        updateReviews(model);
        return "admin::reviews";
    }

    // Вспомогательные методы для уменьшения дублирования кода
    private void populateAdminDashboard(Model model) {
        model.addAttribute("gallery", imagesService.pageList(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
        model.addAttribute("sketches", sketchesService.pageList(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("interestingWorks", interestingWorksService.findAll());
        model.addAttribute("commits", commitsService.findAll().stream()
                .map(commits -> modelMapper.map(commits, CommitsDTO.class)).collect(Collectors.toList()));
        model.addAttribute("carousel", homeService.findAll());
    }

    private void updateGallery(Model model) {
        model.addAttribute("gallery", imagesService.pageList(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
    }

    private void updateSketches(Model model) {
        model.addAttribute("sketches", sketchesService.pageList(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
    }

    private void updateCarousel(Model model) {
        model.addAttribute("carousel", homeService.findAll());
    }

    private void updateInterestingWorks(Model model) {
        model.addAttribute("interestingWorks", interestingWorksService.findAll());
    }

    private void updateReviews(Model model) {
        model.addAttribute("reviews", reviewService.findAll());
    }

}

