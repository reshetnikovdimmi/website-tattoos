package ru.tattoo.maxsim.sitemap;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement(name = "urlset")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlUrlSet {

    @XmlElement(name = "url")
    private Collection<XmlUrl> urls = new ArrayList<>();

    public void addUrl(XmlUrl url) {
        urls.add(url);
    }

    public Collection<XmlUrl> getUrls() {
        return urls;
    }

    public void setUrls(Collection<XmlUrl> urls) {
        this.urls = urls;
    }
}