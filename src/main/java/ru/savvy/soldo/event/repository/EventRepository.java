package ru.savvy.soldo.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.event.model.EventStatus;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    org.springframework.data.domain.Page<Event> findByStatus(EventStatus status, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.startDate >= :date ORDER BY e.startDate")
    List<Event> findUpcoming(LocalDate date);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.startDate BETWEEN :start AND :end")
    List<Event> findByDateRange(LocalDate start, LocalDate end);

    @Query("SELECT e FROM Event e " +
           "WHERE e.status IN (ru.savvy.soldo.event.model.EventStatus.PUBLISHED, ru.savvy.soldo.event.model.EventStatus.DRAFT) " +
           "AND (e.endDate IS NULL OR e.endDate >= :today) " +
           "ORDER BY e.startDate ASC NULLS LAST")
    List<Event> findWidgetEvents(@Param("today") LocalDate today);
}
