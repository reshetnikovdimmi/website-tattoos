package ru.tattoo.maxsim.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "contact_info")
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "tell")
    private String tell;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "map_zoom")
    private Integer mapZoom = 17;

    @Column(name = "work_hours")
    private String workHours; // "Пн-Пт: 09:00-18:00, Сб-Вс: Выходной"

    @Column(name = "map_iframe", columnDefinition = "TEXT")  // 🔥 НОВОЕ ПОЛЕ
    private String mapIframe; // HTML-код iframe для вставки карты
}