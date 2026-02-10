package ru.savvy.soldo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.service.EventService;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository repository;

    @Override
    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    @Override
    public Event saveEvent(Event event) {
        return repository.save(event);
    }

}
