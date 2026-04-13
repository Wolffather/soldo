package ru.savvy.soldo.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.savvy.soldo.booking.model.EventBookingsSummary;

import java.util.Optional;

@Repository
public interface EventBookingSummaryRepository
        extends JpaRepository<EventBookingsSummary, Long> {

    Optional<EventBookingsSummary> findByEventId(Long eventId);

    @Query(value = "SELECT booking_on_create_pending(:eventId)",
            nativeQuery = true)
    void onCreatePending(@Param("eventId") Long eventId);

    
    @Query(value = "SELECT booking_on_create_confirmed(:eventId)",
            nativeQuery = true)
    void onCreateConfirmed(@Param("eventId") Long eventId);

    
    @Query(value = "SELECT booking_on_confirm(:eventId)",
            nativeQuery = true)
    void onConfirm(@Param("eventId") Long eventId);

    
    @Query(value = "SELECT booking_on_cancel_from_confirmed(:eventId)",
            nativeQuery = true)
    void onCancelFromConfirmed(@Param("eventId") Long eventId);

    
    @Query(value = "SELECT booking_on_cancel_from_pending(:eventId)",
            nativeQuery = true)
    void onCancelFromPending(@Param("eventId") Long eventId);

    @Query(value = "SELECT booking_on_delete(:eventId)",
            nativeQuery = true)
    Integer onDelete(@Param("eventId") Long eventId);

    /** Количество свободных мест для события (используется ботом). */
    @Query("SELECT s.numOfParticipants FROM EventBookingsSummary s WHERE s.event.id = :eventId")
    Integer findAvailableSeatsByEventId(@Param("eventId") Long eventId);
}