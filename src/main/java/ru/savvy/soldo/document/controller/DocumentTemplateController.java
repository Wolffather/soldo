package ru.savvy.soldo.document.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.document.dto.DocumentTemplateDTO;
import ru.savvy.soldo.document.service.DocumentTemplateService;

import java.util.List;

@RestController
@RequestMapping("/document-templates")
@RequiredArgsConstructor
public class DocumentTemplateController {

    private final DocumentTemplateService service;

    @GetMapping
    public ResponseEntity<List<DocumentTemplateDTO>> getAll(
            @RequestParam(required = false) String format) {
        return ResponseEntity.ok(service.getAll(format));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentTemplateDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DocumentTemplateDTO> create(@Valid @RequestBody DocumentTemplateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DocumentTemplateDTO> update(
            @PathVariable Long id, @Valid @RequestBody DocumentTemplateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
