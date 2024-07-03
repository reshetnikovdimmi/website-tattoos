package ru.tattoo.maxsim.controller;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.net.MalformedURLException;

@Controller
public class Sitemap {
    @GetMapping("/sitemap")
    public String sitemap() throws MalformedURLException {


        return null;
    }
}