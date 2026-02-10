package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
