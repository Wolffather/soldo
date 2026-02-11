package ru.savvy.soldo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.EventDTO;
import ru.savvy.soldo.mapper.EventMapper;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.service.impl.EventServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventServiceImpl service;

    @Autowired
    private EventMapper mapper;

    @Autowired
    public EventController(EventServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public List<Event> getAll() {
        return service.getAllEvents();
    }

    @PostMapping("/create")
    public Event create(@RequestBody EventDTO eventDTO) {
        Event event = mapper.dtoToEntity(eventDTO);
        return service.saveEvent(event);
    }
}