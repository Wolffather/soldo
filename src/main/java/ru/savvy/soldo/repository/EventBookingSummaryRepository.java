package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.savvy.soldo.model.EventBookingsSummary;

@Repository
public interface EventBookingSummaryRepository
        extends JpaRepository<EventBookingsSummary, Long> {

    
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
}