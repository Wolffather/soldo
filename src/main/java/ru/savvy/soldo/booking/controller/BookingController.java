package ru.savvy.soldo.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.booking.dto.AdminBookingRequest;
import ru.savvy.soldo.booking.dto.PaymentUpdateRequest;
import ru.savvy.soldo.booking.dto.BookingResponse;
import ru.savvy.soldo.booking.dto.BookingSummaryResponse;
import ru.savvy.soldo.booking.service.BookingService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /** Администратор создаёт бронирование вручную (гость без аккаунта) */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> createByAdmin(
            @Valid @RequestBody AdminBookingRequest request) {
        return ResponseEntity.ok(bookingService.createByAdmin(request));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(bookingService.getByEventId(eventId));
    }

    @GetMapping("/event/{eventId}/summary")
    public ResponseEntity<BookingSummaryResponse> getSummary(@PathVariable Long eventId) {
        return ResponseEntity.ok(bookingService.getSummary(eventId));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<BookingSummaryResponse>> getAllSummaries() {
        return ResponseEntity.ok(bookingService.getAllSummaries());
    }

    @GetMapping("/stats/monthly-revenue")
    public ResponseEntity<BigDecimal> getMonthlyRevenue() {
        return ResponseEntity.ok(bookingService.getMonthlyRevenue());
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.confirm(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancel(id));
    }

    @PatchMapping("/{id}/payment")
    public ResponseEntity<BookingResponse> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentUpdateRequest request) {
        return ResponseEntity.ok(bookingService.updatePayment(id, request));
    }
}
