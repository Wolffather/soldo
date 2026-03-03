package ru.savvy.soldo.season.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.season.dto.SeasonDTO;
import ru.savvy.soldo.season.service.SeasonService;

import java.util.List;

@RestController
@RequestMapping("/seasons")
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonService seasonService;

    /** Admin: list all seasons (no sessions nested) */
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping
    public ResponseEntity<List<SeasonDTO>> getAll() {
        return ResponseEntity.ok(seasonService.getAll());
    }

    /** Public: upcoming seasons with sessions, optionally filtered by period (WINTER/SUMMER/ALL_YEAR) */
    @GetMapping("/public")
    public ResponseEntity<List<SeasonDTO>> getPublic(
            @RequestParam(defaultValue = "") String period) {
        return ResponseEntity.ok(seasonService.getUpcomingByPeriod(period));
    }

    /** Admin: get one season with sessions */
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<SeasonDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(seasonService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SeasonDTO> create(@Valid @RequestBody SeasonDTO dto) {
        return ResponseEntity.ok(seasonService.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SeasonDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SeasonDTO dto) {
        return ResponseEntity.ok(seasonService.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seasonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
