package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class Blog{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_name")
    private String imageName;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "section")
    private String section;
    @Lob
    @Column(name = "title")
    private String title;
    @Lob
    @Column(name = "subtitle", columnDefinition = "TEXT")
    private String subtitle;
    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date")
    private Date date;
}
