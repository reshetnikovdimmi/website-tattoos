package ru.tattoo.maxsim.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.tattoo.maxsim.model.ContactInfo;
import ru.tattoo.maxsim.repository.ContactInfoRepository;

@ControllerAdvice
public class GlobalController {
    @Autowired

    private ContactInfoRepository contactInfoRepository;

    @ModelAttribute("servletPath")
    String getRequestServletPath(HttpServletRequest request, Model model) {
        model.addAttribute("contactInfo", contactInfoRepository.findLimit());
            return request.getServletPath();
    }
}