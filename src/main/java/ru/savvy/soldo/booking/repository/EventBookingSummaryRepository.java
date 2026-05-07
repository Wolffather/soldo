package ru.savvy.soldo.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.savvy.soldo.booking.model.EventBookingsSummary;

import java.util.Optional;

@Repository
public interface EventBookingSummaryRepository
        extends JpaRepository<EventBookingsSummary, Long> {

    Optional<EventBookingsSummary> findByEventId(Long eventId);

    /**
     * Пересчитывает все счётчики сводки из реальных данных таблицы bookings.
     * Заменяет набор инкрементальных DB-функций, которые могли рассинхронизироваться.
     */
    @Modifying
    @Query(value = """
            UPDATE event_bookings_summary
            SET
                total_bookings     = (SELECT COUNT(*) FROM bookings WHERE event_id = :eventId),
                confirmed_bookings = (SELECT COUNT(*) FROM bookings WHERE event_id = :eventId AND cancelled = false),
                cancelled_bookings = (SELECT COUNT(*) FROM bookings WHERE event_id = :eventId AND cancelled = true),
                pending_bookings   = 0,
                num_of_participants = GREATEST(0, COALESCE(
                    (SELECT max_participants FROM events WHERE id = :eventId) -
                    (SELECT COUNT(*) FROM bookings WHERE event_id = :eventId AND cancelled = false),
                    0
                )),
                last_updated = NOW()
            WHERE event_id = :eventId
            """, nativeQuery = true)
    void refreshSummary(@Param("eventId") Long eventId);

    /** Количество свободных мест для события (используется виджетом). */
    @Query("SELECT s.numOfParticipants FROM EventBookingsSummary s WHERE s.event.id = :eventId")
    Integer findAvailableSeatsByEventId(@Param("eventId") Long eventId);
}
