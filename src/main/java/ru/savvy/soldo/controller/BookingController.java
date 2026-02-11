package ru.savvy.soldo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.BookingDTO;
import ru.savvy.soldo.mapper.BookingMapper;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.service.impl.BookingServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingServiceImpl service;

    @Autowired
    private BookingMapper mapper;

    @Autowired
    public BookingController(BookingServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public List<Booking> getAllBookingsForUser(@PathVariable("userId") Long userId) {
        return service.findAllUserBookings(userId);
    }

    @PostMapping("/create")
    public Booking createBooking(BookingDTO bookingDTO) {
        Booking booking = mapper.dtoToEntity(bookingDTO);
        return service.createBooking(booking);
    }

    @PatchMapping("/confirm/{bookingId}")
    public Booking confirmBooking(@PathVariable("bookingId") Long bookingId) {
        return service.confirmBooking(bookingId);
    }

    @PatchMapping("/cancel/{bookingId}")
    public Booking cancelBooking(@PathVariable("bookingId") Long bookingId) {
        return service.cancelBooking(bookingId);
    }

    @DeleteMapping("/delete/{bookingId}")
    public void deleteBooking(@PathVariable("bookingId") Long bookingId) {
        service.deleteBooking(bookingId);
    }


}