package ru.savvy.soldo.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.EventDTO;
import ru.savvy.soldo.mapper.EventMapper;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {
    private final EventService service;
    private EventMapper mapper;

    @GetMapping
    public List<Event> getAll() {
        return service.getAllEvents();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public Event create(@Valid @RequestBody EventDTO eventDTO) {
        Event event = mapper.dtoToEntity(eventDTO);
        return service.saveEvent(event);
    }
}