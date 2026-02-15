package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.model.EventCategory;
import ru.savvy.soldo.model.enums.EventFormat;
import ru.savvy.soldo.model.enums.SeasonType;

import java.util.List;
import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    Optional<EventCategory> findByName(String name);

    List<EventCategory> findByFormat(EventFormat format);

    List<EventCategory> findBySeason(SeasonType season);
}