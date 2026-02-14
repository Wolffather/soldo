package ru.savvy.soldo.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.EventBookingsSummary;
import ru.savvy.soldo.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.service.EventService;

import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final EventBookingSummaryRepository summaryRepository;

    @Override
    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Event saveEvent(Event event) {
        Event saved = repository.save(event);
        summaryRepository.save(EventBookingsSummary.of(saved)); // saved!
        return saved;
    }

}
