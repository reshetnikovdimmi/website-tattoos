package ru.tattoo.maxsim.sitemap;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "url")
public class XmlUrl {

    public enum Priority {
        HIGH("1.0"),
        MEDIUM("0.8");

        private final String value;

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
    private String lastmod;

    @XmlElement
    private String changefreq = "weekly"; // Частоту можно изменить на daily или weekly

    @XmlElement
    private String priority;

    public XmlUrl() {}

    public XmlUrl(String loc, Priority priority) {
        this.loc = loc;
        this.lastmod = LocalDateTime.now().toString(); // Динамическая дата обновления
        this.priority = priority.getValue();
    }
}