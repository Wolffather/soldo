package ru.savvy.soldo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.EventCategoryDTO;
import ru.savvy.soldo.model.enums.EventFormat;
import ru.savvy.soldo.model.enums.SeasonType;
import ru.savvy.soldo.service.EventCategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class EventCategoryController {

    private final EventCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<EventCategoryDTO>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventCategoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping("/by-format/{format}")
    public ResponseEntity<List<EventCategoryDTO>> getByFormat(@PathVariable EventFormat format) {
        return ResponseEntity.ok(categoryService.getByFormat(format));
    }

    @GetMapping("/by-season/{season}")
    public ResponseEntity<List<EventCategoryDTO>> getBySeason(@PathVariable SeasonType season) {
        return ResponseEntity.ok(categoryService.getBySeason(season));
    }

    @PostMapping
    public ResponseEntity<EventCategoryDTO> create(@Valid @RequestBody EventCategoryDTO dto) {
        return ResponseEntity.ok(categoryService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventCategoryDTO> update(
            @PathVariable Long id, @Valid @RequestBody EventCategoryDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}