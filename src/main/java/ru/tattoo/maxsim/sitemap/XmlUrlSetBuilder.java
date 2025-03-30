package ru.tattoo.maxsim.sitemap;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class XmlUrlSetBuilder {

    private static final Map<String, XmlUrl.Priority> PRIORITIES = Map.of(
            "/", XmlUrl.Priority.HIGH,
            "/gallery", XmlUrl.Priority.MEDIUM,
            "/contact", XmlUrl.Priority.MEDIUM
    );

    public XmlUrlSet buildSitemap() {
        XmlUrlSet urlset = new XmlUrlSet();
        for (Map.Entry<String, XmlUrl.Priority> entry : PRIORITIES.entrySet()) {
            String path = entry.getKey();
            XmlUrl.Priority priority = entry.getValue();
            urlset.addUrl(createXmlUrl(path, priority));
        }
        return urlset;
    }

    private XmlUrl createXmlUrl(String path, XmlUrl.Priority priority) {
        return new XmlUrl("https://tattoos-maxsim.ru" + path, priority);
    }
}