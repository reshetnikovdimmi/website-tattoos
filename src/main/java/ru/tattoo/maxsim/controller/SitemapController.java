package ru.tattoo.maxsim.controller;

import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tattoo.maxsim.sitemap.XmlUrlSet;
import ru.tattoo.maxsim.sitemap.XmlUrlSetBuilder;

@RestController
public class SitemapController {

    @Autowired
    private XmlUrlSetBuilder xmlUrlSetBuilder;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemap() {
        try {
            String xmlContent = xmlUrlSetBuilder.buildSitemapAsXml();
            return ResponseEntity.ok(xmlContent);
        } catch (JAXBException e) {
            // Логирование ошибки
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>Failed to generate sitemap</error>");
        }
    }

    @GetMapping(value = "/api/sitemap", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<XmlUrlSet> getSitemapJson() {
        XmlUrlSet urlSet = xmlUrlSetBuilder.buildSitemap();
        return ResponseEntity.ok(urlSet);
    }
}