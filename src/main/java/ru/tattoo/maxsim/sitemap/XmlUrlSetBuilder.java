package ru.tattoo.maxsim.sitemap;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Map;

@Component
public class XmlUrlSetBuilder {

    private static final Map<String, UrlConfig> URL_CONFIGS = Map.of(
            "/", new UrlConfig(XmlUrl.Priority.HIGH, XmlUrl.ChangeFreq.DAILY),
            "/gallery", new UrlConfig(XmlUrl.Priority.MEDIUM, XmlUrl.ChangeFreq.WEEKLY),
            "/sketches", new UrlConfig(XmlUrl.Priority.MEDIUM, XmlUrl.ChangeFreq.MONTHLY),
            "/about-me", new UrlConfig(XmlUrl.Priority.MEDIUM, XmlUrl.ChangeFreq.MONTHLY),
            "/blog", new UrlConfig(XmlUrl.Priority.HIGH, XmlUrl.ChangeFreq.WEEKLY),
            "/reviews", new UrlConfig(XmlUrl.Priority.MEDIUM, XmlUrl.ChangeFreq.MONTHLY)
    );

    private static class UrlConfig {
        XmlUrl.Priority priority;
        XmlUrl.ChangeFreq changefreq;

        UrlConfig(XmlUrl.Priority priority, XmlUrl.ChangeFreq changefreq) {
            this.priority = priority;
            this.changefreq = changefreq;
        }
    }

    public XmlUrlSet buildSitemap() {
        XmlUrlSet urlSet = new XmlUrlSet();

        URL_CONFIGS.forEach((path, config) -> {
            XmlUrl url = new XmlUrl(
                    "https://tattoos-maxsim.ru" + path,
                    config.priority,
                    config.changefreq
            );
            urlSet.addUrl(url);
        });

        return urlSet;
    }

    public String toXmlString(XmlUrlSet urlSet) throws JAXBException {
        if (urlSet == null) {
            return "";
        }

        JAXBContext context = JAXBContext.newInstance(XmlUrlSet.class);
        Marshaller marshaller = context.createMarshaller();

        // Форматирование XML
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        // Добавляем XML заголовок
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);

        StringWriter writer = new StringWriter();
        marshaller.marshal(urlSet, writer);

        return writer.toString();
    }

    // Альтернативный метод для удобства
    public String buildSitemapAsXml() throws JAXBException {
        XmlUrlSet urlSet = buildSitemap();
        return toXmlString(urlSet);
    }
}