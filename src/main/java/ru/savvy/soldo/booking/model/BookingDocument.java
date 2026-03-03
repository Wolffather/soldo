package ru.savvy.soldo.booking.model;

import jakarta.persistence.*;
import lombok.*;

import ru.savvy.soldo.document.model.DocumentTemplate;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_template_id")
    private DocumentTemplate documentTemplate;

    @Builder.Default
    private Boolean delivered = false;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
