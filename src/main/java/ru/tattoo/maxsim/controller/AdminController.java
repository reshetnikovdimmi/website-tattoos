package ru.tattoo.maxsim.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.model.DTO.CommitsDTO;
import ru.tattoo.maxsim.model.Home;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.repository.ContactInfoRepository;
import ru.tattoo.maxsim.service.interf.*;
import ru.tattoo.maxsim.util.PageSize;

import java.io.IOException;
import java.text.ParseException;
import java.util.stream.Collectors;


@Controller
@RequestMapping(AdminController.ADMIN_URL)
public class AdminController extends CRUDController<Home, Long> {

    private static final String ALL_GALLERY = "Вся галерея";
    private static final int PAGE_NUMBER = 0;
    public static final String ADMIN_URL = "/admin";
    public static final String ADMIN_NAME = "admin";


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
    public String showPage(Model model) {
        populateAdminDashboard(model);
        return getEntityName();
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





    @PostMapping("/interesting-works-import")
    public String uploadInterestingWork(@RequestParam("file") MultipartFile fileImport,
                                        @RequestParam("description") String description,
                                        Model model) throws IOException, ParseException {
        interestingWorksService.saveInterestingWorks(fileImport, description);
        updateInterestingWorks(model);
        return "admin::interesting-works-import";
    }

    @GetMapping("/interesting-works-delete/{id}")
    public String deleteInterestingWork(@PathVariable("id") Long id, Model model) throws IOException, ParseException {
        interestingWorksService.deleteInterestingWorks(id);
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
        model.addAttribute("gallery", imagesService.getGalleryDto(style.equals(ALL_GALLERY) ? null : style, null, number, page));
        return "admin::img-import";
    }

    @GetMapping("/sketches/{page}/{number}")
    public String searchSketches(@PathVariable("page") int page,
                                 @PathVariable("number") int number,
                                 Model model) {
        model.addAttribute("sketches", sketchesService.getSketchesDto(null, null, number, page));
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
        model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
        model.addAttribute("sketches", sketchesService.getSketchesDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
        model.addAttribute("reviews", reviewService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("interestingWorks", interestingWorksService.findAll());
        model.addAttribute("commits", commitsService.findAll().stream()
                .map(commits -> modelMapper.map(commits, CommitsDTO.class)).collect(Collectors.toList()));
        model.addAttribute("home", getService().findAll());
        System.out.println(getService().findAll());

    }

    private void updateGallery(Model model) {
        model.addAttribute("gallery", imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
    }

    private void updateSketches(Model model) {
        model.addAttribute("sketches", sketchesService.getSketchesDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER));
    }

    private void updateCarousel(Model model) {
        model.addAttribute("category", homeService.findByCategory("carousel-import"));
        model.addAttribute("home", homeService.findAll());
    }

    private void updateInterestingWorks(Model model) {
        model.addAttribute("interestingWorks", interestingWorksService.findAll());
    }

    private void updateReviews(Model model) {
        model.addAttribute("reviews", reviewService.findAll());
    }


    @PostMapping(path = "/best-tattoos")
    private ResponseEntity bestImage(@RequestBody Images images) {
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

    @Override
    String getEntityName() {
        return ADMIN_NAME;
    }

    @Override
    CRUDService<Home, Long> getService() {
        return homeService;
    }
}

