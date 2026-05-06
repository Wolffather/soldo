package ru.savvy.soldo.booking.service;

import ru.savvy.soldo.booking.dto.BookingDocumentResponse;
import ru.savvy.soldo.booking.model.Booking;

import java.util.List;

public interface BookingDocumentService {

    /** Creates document stubs for a new booking based on templates linked to its event. */
    void createDocumentsForBooking(Booking booking);

    /** Sends (or re-sends) document email for a booking. Updates emailSentAt on all documents. */
    void sendDocumentEmail(Booking booking);

    /** Archives all documents for a booking (called when booking is cancelled). */
    void archiveDocumentsForBooking(Long bookingId);

    /** Returns all documents for a specific booking (including archived). For admin use. */
    List<BookingDocumentResponse> getForBooking(Long bookingId);
}
