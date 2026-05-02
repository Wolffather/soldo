package ru.savvy.soldo.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.savvy.soldo.booking.model.BookingStatus;
import ru.savvy.soldo.booking.model.PaymentStatus;
import ru.savvy.soldo.event.model.Event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Контактное имя для бронирования (бронирования — всегда гостевые) */
    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "guest_phone")
    private String guestPhone;

    @Column(name = "guest_email")
    private String guestEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.NOT_REQUIRED;

    @Column(name = "amount_due", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal amountDue = BigDecimal.ZERO;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "payment_deadline")
    private LocalDate paymentDeadline;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    private String notes;

    /** Есть ли у участника сертификат ПФДО. Влияет на amountDue при создании бронирования. */
    @Column(name = "has_certificate", nullable = false)
    @Builder.Default
    private boolean hasCertificate = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}