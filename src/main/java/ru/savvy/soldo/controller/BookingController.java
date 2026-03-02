package ru.savvy.soldo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.*;
import ru.savvy.soldo.dto.request.PaymentUpdateRequest;
import ru.savvy.soldo.dto.response.BookingResponse;
import ru.savvy.soldo.dto.response.BookingSummaryResponse;
import ru.savvy.soldo.service.BookingService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @Valid @RequestBody BookingDTO dto,
            Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(bookingService.create(dto, userId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(bookingService.getByEventId(eventId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(bookingService.getByUserId(userId));
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