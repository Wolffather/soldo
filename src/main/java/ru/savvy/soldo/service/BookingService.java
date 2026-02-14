package ru.savvy.soldo.service;

import ru.savvy.soldo.model.Booking;

import java.util.List;

public interface BookingService {
    Booking findBookingById(Long bookingId);
    List<Booking> findAllUserBookings(Long userId);
    Booking createBooking(Booking booking);
    Booking confirmBooking(Booking booking);
    Booking cancelBooking(Booking booking);
    void deleteBooking(Booking booking);
}