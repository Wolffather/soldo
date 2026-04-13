package ru.savvy.soldo.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.booking.dto.BookingDocumentResponse;
import ru.savvy.soldo.booking.service.BookingDocumentService;

import java.util.List;

/**
 * Admin API for viewing booking documents.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDocumentController {

    private final BookingDocumentService bookingDocumentService;

    /**
     * Returns all documents for a specific booking (including archived).
     */
    @GetMapping("/{bookingId}/documents")
    public ResponseEntity<List<BookingDocumentResponse>> getBookingDocuments(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingDocumentService.getForBooking(bookingId));
    }
}
