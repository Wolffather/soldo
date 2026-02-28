package ru.savvy.soldo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.enums.EventStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category WHERE e.id = :id")

    Optional<Event> findByIdWithCategory(Long id);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category")
    Page<Event> findAllWithCategory(Pageable pageable);

    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Page<Event> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.startDate >= :date ORDER BY e.startDate")
    List<Event> findUpcoming(LocalDate date);

    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.startDate BETWEEN :start AND :end")
    List<Event> findByDateRange(LocalDate start, LocalDate end);

}