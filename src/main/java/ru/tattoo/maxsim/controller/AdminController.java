package ru.tattoo.maxsim.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.model.Sketches;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.service.interf.*;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.text.ParseException;


@Controller

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


    @GetMapping("/admin")
    public String admin(Model model) {

        Pageable p = PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize());
        Page<Images> images = imagesService.partition(p);
        Page<Sketches> sketches = sketchesService.partition(p);

        model.addAttribute("images", imagesService.findAll());
        model.addAttribute("sketches", sketchesService.pageList(sketches));
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("user", userService.findAll());
        model.addAttribute("flag", true);
        model.addAttribute("interestingWorks", interestingWorksService.findAll());
        model.addAttribute("commits", commitsService.findAll());
        model.addAttribute("carousel", homeService.findAll());



        model.addAttribute("number", PageSize.IMG_9.getPageSize());
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("imagesGallery", imagesService.pageList(images));
        model.addAttribute("options", PageSize.getLisPageSize());

        return "admin";
    }

    @PostMapping("/img-import")

    public String imgImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description,@RequestParam("category") String category, Model model, HttpServletRequest request) throws IOException, ParseException {

        imagesService.saveImg(fileImport,description,category);
        model.addAttribute("images", imagesService.findAll());
        Pageable p = PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize());
        Page<Images> images = imagesService.partition(p);

        model.addAttribute("number", PageSize.IMG_9.getPageSize());
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("imagesGallery", imagesService.pageList(images));
        model.addAttribute("options", PageSize.getLisPageSize());
        return "admin::img-import";
    }

    @PostMapping("/sketches-import")
    public String sketchesImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description, Model model, HttpServletRequest request) throws IOException, ParseException {

        sketchesService.saveImg(fileImport,description);
        Pageable p = PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize());
        Page<Sketches> sketches = sketchesService.partition(p);
        model.addAttribute("sketches", sketchesService.pageList(sketches));
        model.addAttribute("page", sketches.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", sketches.getTotalElements());

        model.addAttribute("options", PageSize.getLisPageSize());

        return "admin::sketches-import";
    }

    @PostMapping("/carousel-import")
    public String carouselImport(@RequestParam("file") MultipartFile fileImport, Model model, HttpServletRequest request) throws IOException, ParseException {

        homeService.saveImg(fileImport, "carousel");
        model.addAttribute("carousel", homeService.findAll());

        return "admin::carousel-import";
    }

    @GetMapping("/carousel-delete/{id}")
    public String deleteCarousel(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {
        homeService.deleteImg(id);
        model.addAttribute("carousel", homeService.findAll());

        return "admin::carousel-import";
    }

    @PostMapping("/interesting-works-import")
    public String interestingWorksImport(@RequestParam("file") MultipartFile fileImport, @RequestParam("description") String description, Model model, HttpServletRequest request) throws IOException, ParseException {

        interestingWorksService.saveImg(fileImport,description);
        model.addAttribute("interestingWorks", interestingWorksService.findAll());

        return "admin::interesting-works-import";
    }

    @GetMapping("/interesting-works-delete/{id}")
    public String deleteInterestingWorks(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        interestingWorksService.deleteImg(id);
        model.addAttribute("interestingWorks", interestingWorksService.findAll());

        return "admin::interesting-works-import";
    }

    @GetMapping("/sketches-delete/{id}")
    public String deleteSketches(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        sketchesService.deleteImg(id);

        Pageable p = PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize());
        Page<Sketches> sketches = sketchesService.partition(p);
        model.addAttribute("sketches", sketchesService.pageList(sketches));
        model.addAttribute("page", sketches.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", sketches.getTotalElements());

        model.addAttribute("options", PageSize.getLisPageSize());

        return "admin::sketches-import";
    }

    @GetMapping("/img-delete/{id}")
    public String deleteImg(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {
        System.out.println(id);
        imagesService.deleteImg(id);
        model.addAttribute("images", imagesService.findAll());
        Pageable p = PageRequest.of(PAGE_NUMBER, PageSize.IMG_9.getPageSize());
        Page<Images> images = imagesService.partition(p);

        model.addAttribute("number", PageSize.IMG_9.getPageSize());
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", PAGE_NUMBER);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("imagesGallery", imagesService.pageList(images));
        model.addAttribute("options", PageSize.getLisPageSize());
        return "admin::img-import";
    }

    @RequestMapping(value = "/admin/{style}/{page}/{number}", method = RequestMethod.GET)

    private String gallerySearch(@PathVariable("style") String style, @PathVariable("page") int page, @PathVariable("number") int number, Model model) {

        Page<Images> images;
        Pageable p = PageRequest.of(page, number);
        if (style.equals(ALL_GALLERY)) {
            images = imagesService.partition(p);
        } else {
            images = imagesService.findByCategory(style, p);
        }
        model.addAttribute("imagesGallery", imagesService.pageList(images));
        model.addAttribute("number", number);
        model.addAttribute("page", images.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("imagesTotal", images.getTotalElements());
        model.addAttribute("options", PageSize.getLisPageSize());

        return "admin::img-import";
    }

    @GetMapping("/reviews-delete/{id}")
    public String deleteReviews(@PathVariable("id") Long id, Model model, HttpServletRequest request) throws IOException, ParseException {

        reviewService.deleteImg(id);
        model.addAttribute("reviews", reviewService.findAll());

        return "admin::reviews";
    }


    //Response controller

    @PostMapping(path = "/best-tattoos")
    private ResponseEntity saveSparkSale(@RequestBody Images images) {
        return ResponseEntity.ok(imagesService.bestImage(images));
    }


    @PostMapping(path = "/contact-info")
    private ResponseEntity<ContactInfo> contactInfo(@RequestBody ContactInfo newContact, Model model) {
        ContactInfo contactInfo = contactInfoRepository.findLimit();
        if (newContact.getTell()!=null) contactInfo.setTell(newContact.getTell());
        if (newContact.getEmail()!=null) contactInfo.setEmail(newContact.getEmail());
        if (newContact.getAddress()!=null) contactInfo.setAddress(newContact.getAddress());
        contactInfoRepository.save(contactInfo);

        return ResponseEntity.ok(contactInfoRepository.findLimit());
    }

}

