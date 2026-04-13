package ru.savvy.soldo.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.event.dto.EventCategoryDTO;
import ru.savvy.soldo.event.model.EventFormat;
import ru.savvy.soldo.event.service.EventCategoryService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class EventCategoryController {

    private final EventCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<EventCategoryDTO>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/formats")
    public ResponseEntity<List<Map<String, String>>> getFormats() {
        List<Map<String, String>> formats = Arrays.stream(EventFormat.values())
                .map(f -> Map.of("value", f.name(), "label", f.getLabel()))
                .toList();
        return ResponseEntity.ok(formats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventCategoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping("/by-format/{format}")
    public ResponseEntity<List<EventCategoryDTO>> getByFormat(@PathVariable EventFormat format) {
        return ResponseEntity.ok(categoryService.getByFormat(format));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PostMapping
    public ResponseEntity<EventCategoryDTO> create(@Valid @RequestBody EventCategoryDTO dto) {
        return ResponseEntity.ok(categoryService.create(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<EventCategoryDTO> update(
            @PathVariable Long id, @Valid @RequestBody EventCategoryDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
