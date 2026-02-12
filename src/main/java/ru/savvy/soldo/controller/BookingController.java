package ru.savvy.soldo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.BookingDTO;
import ru.savvy.soldo.mapper.BookingMapper;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.service.impl.BookingServiceImpl;
import ru.savvy.soldo.service.impl.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingServiceImpl bookingService;
    private final UserServiceImpl userService;

    @Autowired
    private BookingMapper mapper;

    @Autowired
    public BookingController(BookingServiceImpl bookingService, UserServiceImpl userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    private Long getCurrentUserId(@AuthenticationPrincipal UserDetails currentUser) {
        return userService.findByUsername(currentUser.getUsername()).getId();
    }

    private void checkUserAccess(Long currentUserId, Long resourceUserId) {
        if (!currentUserId.equals(resourceUserId)) {
            throw new SecurityException("Доступ запрещен");
        }
    }

    @GetMapping("/{userId}")
    public List<Booking> getAllBookingsForUser(@PathVariable("userId") Long userId,
                                               @AuthenticationPrincipal UserDetails currentUser) {
        Long currentUserId = getCurrentUserId(currentUser);
        checkUserAccess(currentUserId, userId);
        return bookingService.findAllUserBookings(userId);
    }

    @PostMapping("/create")
    public Booking createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        Booking booking = mapper.dtoToEntity(bookingDTO);
        return bookingService.createBooking(booking);
    }

    @PatchMapping("/confirm/{bookingId}")
    public Booking confirmBooking(@PathVariable("bookingId") Long bookingId,
                                  @AuthenticationPrincipal UserDetails currentUser) {
        Long currentUserId = getCurrentUserId(currentUser);
        Long bookedUserId = bookingService.findBookingById(bookingId).getUserId();
        checkUserAccess(currentUserId, bookedUserId);
        return bookingService.confirmBooking(bookingId);
    }

    @PatchMapping("/cancel/{bookingId}")
    public Booking cancelBooking(@PathVariable("bookingId") Long bookingId,
                                 @AuthenticationPrincipal UserDetails currentUser) {
        Long currentUserId = getCurrentUserId(currentUser);
        Long bookedUserId = bookingService.findBookingById(bookingId).getUserId();
        checkUserAccess(currentUserId, bookedUserId);
        return bookingService.cancelBooking(bookingId);
    }

    @DeleteMapping("/delete/{bookingId}")
    public void deleteBooking(@PathVariable("bookingId") Long bookingId,
                              @AuthenticationPrincipal UserDetails currentUser) {
        Long currentUserId = getCurrentUserId(currentUser);
        Long bookedUserId = bookingService.findBookingById(bookingId).getUserId();
        checkUserAccess(currentUserId, bookedUserId);
        bookingService.deleteBooking(bookingId);
    }
}