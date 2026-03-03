package ru.savvy.soldo.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.booking.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /** Загружаем бронирования с event и category одним запросом — избегаем LazyInitializationException. */
    @Query("SELECT b FROM Booking b JOIN FETCH b.event e LEFT JOIN FETCH e.category WHERE b.event.id = :eventId")
    List<Booking> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.event e LEFT JOIN FETCH e.category WHERE b.user.id = :userId")
    List<Booking> findByUserId(@Param("userId") Long userId);

    List<Booking> findByEventIdAndStatus(Long eventId, BookingStatus status);

    boolean existsByUserIdAndEventIdAndStatusNot(Long userId, Long eventId, BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.event e LEFT JOIN FETCH e.category " +
           "WHERE b.paymentStatus = 'PENDING' " +
           "AND b.paymentDeadline <= :deadline AND b.status = ru.savvy.soldo.booking.model.BookingStatus.CONFIRMED")
    List<Booking> findUnpaidWithDeadlineBefore(@Param("deadline") LocalDate deadline);

    @Query("SELECT COALESCE(SUM(b.amountPaid), 0) FROM Booking b " +
            "WHERE b.paymentStatus = ru.savvy.soldo.booking.model.PaymentStatus.PAID " +
            "AND b.paymentDate >= :startOfMonth")
    BigDecimal getMonthlyRevenue(@Param("startOfMonth") LocalDateTime startOfMonth);

    @Query("SELECT b FROM Booking b JOIN FETCH b.event e LEFT JOIN FETCH e.category WHERE b.id = :id")
    Optional<Booking> findByIdWithCategory(@Param("id") Long id);
}
