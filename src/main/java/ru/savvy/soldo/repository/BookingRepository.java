package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.model.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByEventId(Long eventId);

    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventIdAndStatus(Long eventId, BookingStatus status);

    boolean existsByUserIdAndEventIdAndStatusNot(Long userId, Long eventId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.paymentStatus = 'PENDING' " +
            "AND b.paymentDeadline <= :deadline AND b.status = ru.savvy.soldo.model.enums.BookingStatus.CONFIRMED")
    List<Booking> findUnpaidWithDeadlineBefore(LocalDate deadline);
}