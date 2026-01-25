package ru.tattoo.maxsim.sitemap;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "url")
public class XmlUrl {

    public enum Priority {
        HIGH("1.0"),
        MEDIUM("0.8"),
        LOW("0.5");

        private final String value;

        Priority(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ChangeFreq {
        ALWAYS("always"),
        HOURLY("hourly"),
        DAILY("daily"),
        WEEKLY("weekly"),
        MONTHLY("monthly"),
        YEARLY("yearly"),
        NEVER("never");

        private final String value;

        ChangeFreq(String value) {
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
    private String changefreq;

    @XmlElement
    private String priority;

    public XmlUrl(String loc, Priority priority, ChangeFreq changefreq) {
        this.loc = loc;
        this.lastmod = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        this.priority = priority.getValue();
        this.changefreq = changefreq.getValue();
    }

    public XmlUrl(String loc, Priority priority) {
        this(loc, priority, ChangeFreq.WEEKLY);
    }
}