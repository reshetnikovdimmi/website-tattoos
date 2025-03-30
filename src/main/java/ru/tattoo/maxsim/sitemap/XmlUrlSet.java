

package ru.tattoo.maxsim.sitemap;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "urlset", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
public class XmlUrlSet {

    @XmlElements({
            @XmlElement(name = "url", type = XmlUrl.class)
    })
    private Collection<XmlUrl> urls = new ArrayList<>();

    public void addUrl(XmlUrl url) {
        urls.add(url);
    }

    public Collection<XmlUrl> getUrls() {
        return urls;
    }
}
