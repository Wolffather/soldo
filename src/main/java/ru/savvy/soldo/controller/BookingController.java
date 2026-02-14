package ru.savvy.soldo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.BookingDTO;
import ru.savvy.soldo.dto.BookingResponse;
import ru.savvy.soldo.mapper.BookingMapper;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.service.BookingService;
import ru.savvy.soldo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final BookingMapper mapper;

    public BookingController(BookingService bookingService,
                             UserService userService,
                             BookingMapper mapper) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.mapper = mapper;
    }

    private Long getCurrentUserId(Authentication auth) {
        return Long.parseLong((String) auth.getPrincipal());
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        List<Booking> bookings = bookingService.findAllUserBookings(userId);
        return ResponseEntity.ok(mapper.entitiesToResponses(bookings));
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingDTO bookingDTO,
            Authentication auth) {
        Long userId = getCurrentUserId(auth);
        Booking booking = mapper.dtoToEntity(bookingDTO);
        booking.setUser(userService.findById(userId));
        Booking saved = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.entityToResponse(saved));
    }

    @PatchMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable Long bookingId,
            Authentication auth) {
        Booking booking = bookingService.findBookingById(bookingId);
        checkAccess(auth, booking.getUser().getId());
        Booking confirmed = bookingService.confirmBooking(booking);
        return ResponseEntity.ok(mapper.entityToResponse(confirmed));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            Authentication auth) {
        Booking booking = bookingService.findBookingById(bookingId);
        checkAccess(auth, booking.getUser().getId());
        Booking cancelled = bookingService.cancelBooking(booking);
        return ResponseEntity.ok(mapper.entityToResponse(cancelled));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable Long bookingId,
            Authentication auth) {
        Booking booking = bookingService.findBookingById(bookingId);
        checkAccess(auth, booking.getUser().getId());
        bookingService.deleteBooking(booking);
        return ResponseEntity.noContent().build();
    }

    private void checkAccess(Authentication auth, Long resourceUserId) {
        Long currentUserId = getCurrentUserId(auth);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !currentUserId.equals(resourceUserId)) {
            throw new AccessDeniedException("Доступ запрещён");
        }
    }
}