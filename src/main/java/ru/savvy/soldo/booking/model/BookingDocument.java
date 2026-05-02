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

    /** Помечается true при отмене бронирования — документ скрывается в кабинете */
    @Builder.Default
    private Boolean archived = false;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    /** ФИО подписанта, введённое при электронном подписании */
    @Column(name = "signer_name")
    private String signerName;

    /** Дата и время подписания */
    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    /** IP-адрес подписанта на момент подписания */
    @Column(name = "signer_ip")
    private String signerIp;

    /** Данные сторон договора (паспорт, адрес) в формате JSON, зафиксированные при подписании */
    @Column(name = "filled_data", columnDefinition = "TEXT")
    private String filledData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
