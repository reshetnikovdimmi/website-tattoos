package ru.tattoo.maxsim.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tattoo.maxsim.controller.CRUDController;
import ru.tattoo.maxsim.model.ChooseusSection;
import ru.tattoo.maxsim.model.Video;
import ru.tattoo.maxsim.service.interf.CRUDService;
import ru.tattoo.maxsim.service.interf.HomeService;
import ru.tattoo.maxsim.service.interf.VideoService;

@Controller
@RequestMapping(VideoController.URL)
public class VideoController extends CRUDController<Video, Long> {

    public static final String URL = "/admin/videos";
    public static final String PAGE_FRAGMENT = "fragment-admin";

    @Autowired
    private VideoService videoService;
    @Autowired
    private HomeService homeService;

    @Override
    protected String getFragmentName() {
        return PAGE_FRAGMENT;
    }

    @Override
    protected CRUDService<Video, Long> getService() {
        return videoService;
    }

    @Override
    protected void updateSection(Model model) {
        model.addAttribute("videos", new Video());
        model.addAttribute("home", homeService.findAll());
    }
}
