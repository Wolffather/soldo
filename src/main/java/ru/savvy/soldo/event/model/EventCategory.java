package ru.savvy.soldo.event.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import ru.savvy.soldo.event.model.EventFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_categories")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventFormat format;

    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
}