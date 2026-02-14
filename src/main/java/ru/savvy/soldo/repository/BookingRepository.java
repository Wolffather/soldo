package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.savvy.soldo.model.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"user", "event"})
    List<Booking> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "event"})
    Optional<Booking> findById(Long id);

    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.user.id = :userId
          AND b.event.id = :eventId
          AND b.status <> ru.savvy.soldo.enums.BookingStatus.CANCELLED
    """)
    boolean existsActiveBooking(@Param("userId") Long userId,
                                @Param("eventId") Long eventId);
}