package ru.savvy.soldo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.EventBookingsSummary;
import ru.savvy.soldo.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.service.EventService;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final EventBookingSummaryRepository summaryRepository;

    @Autowired
    public EventServiceImpl(EventRepository repository, EventBookingSummaryRepository summaryRepository) {
        this.repository = repository;
        this.summaryRepository = summaryRepository;
    }

    @Override
    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    @Override
    public Event saveEvent(Event event) {
        Event saved = repository.save(event);

        summaryRepository.save(EventBookingsSummary.of(event));
        return saved;
    }

}
