package ru.savvy.soldo.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.event.dto.EventDTO;
import ru.savvy.soldo.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    @GetMapping
    public ResponseEntity<Page<EventDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").ascending());
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDTO>> getUpcoming(
            @RequestParam(defaultValue = "ALL_YEAR") String season) {
        return ResponseEntity.ok(service.getUpcoming(season));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventDTO> create(@Valid @RequestBody EventDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EventDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}