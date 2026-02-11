package ru.savvy.soldo.service;

import ru.savvy.soldo.model.Booking;

import java.util.List;

public interface BookingService {

    Booking findBookingById(Long bookingId);

    List<Booking> findAllUserBookings(Long userId);

    Booking createBooking(Booking booking);

    Booking confirmBooking(Long bookingId);

    Booking cancelBooking(Long bookingId);

    void deleteBooking(Long bookingId);

}
