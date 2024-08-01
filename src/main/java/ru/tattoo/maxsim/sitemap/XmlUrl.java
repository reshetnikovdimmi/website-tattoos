package ru.tattoo.maxsim.sitemap;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@XmlAccessorType(value = XmlAccessType.NONE)
@XmlRootElement(name = "url")

public class XmlUrl {
    public enum Priority {
        HIGH("1.0"), MEDIUM("0.1");

        private String value;

        Priority(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @XmlElement
    private String loc;

    @XmlElement
    private String lastmod = String.valueOf(LocalDateTime.now());

    @XmlElement
    private String changefreq = "daily";

    @XmlElement
    private String priority;

    public XmlUrl() {
    }

    public XmlUrl(String loc, Priority priority) {
        this.loc = loc;
        this.priority = priority.getValue();
    }

}