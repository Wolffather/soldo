package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.model.EventBookingsSummary;

public interface EventBookingSummaryRepository extends JpaRepository<EventBookingsSummary, Long> {
}
