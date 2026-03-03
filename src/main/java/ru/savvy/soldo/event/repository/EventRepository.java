package ru.savvy.soldo.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.event.model.EventStatus;
import ru.savvy.soldo.season.model.SeasonType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category LEFT JOIN FETCH e.season WHERE e.id = :id")
    Optional<Event> findByIdWithCategory(Long id);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category LEFT JOIN FETCH e.season")
    Page<Event> findAllWithCategory(Pageable pageable);

    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Page<Event> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.startDate >= :date ORDER BY e.startDate")
    List<Event> findUpcoming(LocalDate date);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.startDate BETWEEN :start AND :end")
    List<Event> findByDateRange(LocalDate start, LocalDate end);

    /**
     * Fetch published regular (non-session) events for a given seasonal period.
     * Excludes events linked to a season (SESSION_OUTDOOR / SESSION_CITY type).
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category c " +
           "WHERE e.status = ru.savvy.soldo.event.model.EventStatus.PUBLISHED " +
           "AND e.season IS NULL " +
           "AND c.season = :season " +
           "AND c.format NOT IN (ru.savvy.soldo.event.model.EventFormat.SESSION_OUTDOOR, " +
           "                     ru.savvy.soldo.event.model.EventFormat.SESSION_CITY) " +
           "AND e.startDate IS NOT NULL AND e.startDate >= :date")
    List<Event> findUpcomingBySeason(@Param("season") SeasonType season,
                                     @Param("date") LocalDate date);

    /**
     * Fetch published ALL_YEAR regular events (not linked to a season).
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category c " +
           "WHERE e.status = ru.savvy.soldo.event.model.EventStatus.PUBLISHED " +
           "AND e.season IS NULL " +
           "AND (c.season = ru.savvy.soldo.season.model.SeasonType.ALL_YEAR OR e.category IS NULL) " +
           "AND (c.format IS NULL OR c.format NOT IN (ru.savvy.soldo.event.model.EventFormat.SESSION_OUTDOOR, " +
           "                                          ru.savvy.soldo.event.model.EventFormat.SESSION_CITY)) " +
           "AND e.startDate IS NOT NULL AND e.startDate >= :date")
    List<Event> findUpcomingYearRound(@Param("date") LocalDate date);

    /**
     * Fetch published sessions (смены) linked to a specific season,
     * ordered by start date ascending.
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category " +
           "WHERE e.season.id = :seasonId " +
           "AND e.status = ru.savvy.soldo.event.model.EventStatus.PUBLISHED " +
           "ORDER BY e.startDate ASC NULLS LAST")
    List<Event> findBySeasonId(@Param("seasonId") Long seasonId);

    /**
     * Fetch ALL sessions (any status) linked to a specific season for admin view.
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category " +
           "WHERE e.season.id = :seasonId " +
           "ORDER BY e.startDate ASC NULLS LAST")
    List<Event> findAllBySeasonId(@Param("seasonId") Long seasonId);

    /**
     * Check if any session of the given season overlaps with [start, end].
     */
    @Query("SELECT COUNT(e) > 0 FROM Event e " +
           "WHERE e.season.id = :seasonId " +
           "AND (:excludeId IS NULL OR e.id <> :excludeId) " +
           "AND e.startDate IS NOT NULL AND e.endDate IS NOT NULL " +
           "AND e.startDate <= :end AND e.endDate >= :start")
    boolean hasOverlappingSession(@Param("seasonId") Long seasonId,
                                   @Param("start") LocalDate start,
                                   @Param("end") LocalDate end,
                                   @Param("excludeId") Long excludeId);
}
