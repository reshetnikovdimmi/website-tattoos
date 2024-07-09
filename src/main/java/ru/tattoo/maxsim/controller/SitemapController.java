package ru.tattoo.maxsim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.tattoo.maxsim.sitemap.XmlUrl;
import ru.tattoo.maxsim.sitemap.XmlUrlSet;

import java.net.MalformedURLException;

@Controller
public class SitemapController {
    @GetMapping("/sitemap")
    @ResponseBody

    public XmlUrlSet sitemap() throws MalformedURLException {
        XmlUrlSet urlset = new XmlUrlSet();
        create(urlset, "/", XmlUrl.Priority.HIGH);
        create(urlset, "/gallery", XmlUrl.Priority.MEDIUM);
        create(urlset, "/contact", XmlUrl.Priority.MEDIUM);

        return urlset;
    }

    private void create(XmlUrlSet xmlUrlSet, String link, XmlUrl.Priority priority) {
        xmlUrlSet.addUrl(new XmlUrl("https://tattoos-maxsim.ru" + link, priority));
    }
}