package ru.savvy.soldo.inquiry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.inquiry.dto.InquiryDto;
import ru.savvy.soldo.inquiry.service.InquiryService;

@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    /** Публичный эндпоинт — форма обратной связи с сайта */
    @PostMapping("/public/inquiries")
    public ResponseEntity<Void> create(@Valid @RequestBody InquiryDto dto) {
        inquiryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** Только для администратора — список заявок в админ-панели */
    @GetMapping("/inquiries")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<InquiryDto> getAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return inquiryService.getAll(pageable);
    }

    /** Только для администратора — удаление заявки */
    @DeleteMapping("/inquiries/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inquiryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
