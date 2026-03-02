package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.savvy.soldo.model.Season;
import ru.savvy.soldo.model.enums.EventStatus;
import ru.savvy.soldo.model.enums.SeasonType;

import java.util.List;

public interface SeasonRepository extends JpaRepository<Season, Long> {

    List<Season> findAllByOrderByYearDescCreatedAtDesc();

    /**
     * Fetch published seasons with year >= currentYear for a specific period,
     * ordered by year ascending.
     */
    @Query("SELECT s FROM Season s WHERE s.status = :status AND s.year >= :year " +
           "AND (:period IS NULL OR s.period = :period) " +
           "ORDER BY s.year ASC, s.createdAt DESC")
    List<Season> findUpcoming(@Param("status") EventStatus status,
                               @Param("year") int year,
                               @Param("period") SeasonType period);
}
