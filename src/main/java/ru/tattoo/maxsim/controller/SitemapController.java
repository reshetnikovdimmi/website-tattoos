package ru.tattoo.maxsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.tattoo.maxsim.sitemap.XmlUrl;
import ru.tattoo.maxsim.sitemap.XmlUrlSet;
import ru.tattoo.maxsim.sitemap.XmlUrlSetBuilder;

import java.net.MalformedURLException;

@Controller
public class SitemapController {

    private final XmlUrlSetBuilder xmlUrlSetBuilder;

    public SitemapController(XmlUrlSetBuilder xmlUrlSetBuilder) {
        this.xmlUrlSetBuilder = xmlUrlSetBuilder;
    }

    @GetMapping("/sitemap")
    @ResponseBody
    public XmlUrlSet sitemap() throws MalformedURLException {
        return xmlUrlSetBuilder.buildSitemap();
    }
}