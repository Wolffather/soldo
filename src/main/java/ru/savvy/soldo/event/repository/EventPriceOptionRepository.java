package ru.savvy.soldo.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.event.model.EventPriceOption;

import java.util.List;

public interface EventPriceOptionRepository extends JpaRepository<EventPriceOption, Long> {
    List<EventPriceOption> findByEventIdOrderBySortOrderAscIdAsc(Long eventId);
    void deleteByEventId(Long eventId);
}
