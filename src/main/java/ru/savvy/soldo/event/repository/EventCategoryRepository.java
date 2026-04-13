package ru.savvy.soldo.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.event.model.EventCategory;
import ru.savvy.soldo.event.model.EventFormat;

import java.util.List;
import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    Optional<EventCategory> findByName(String name);

    List<EventCategory> findByFormat(EventFormat format);
}
