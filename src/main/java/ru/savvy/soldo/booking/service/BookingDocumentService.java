package ru.savvy.soldo.booking.service;

import ru.savvy.soldo.booking.dto.BookingDocumentResponse;
import ru.savvy.soldo.booking.model.Booking;

import java.util.List;

public interface BookingDocumentService {

    /**
     * Creates document stubs (based on templates) for a booking.
     *
     * @param booking        the newly created booking
     * @param categoryFormat the format name of the event category
     */
    void createDocumentsForBooking(Booking booking, String categoryFormat);

    /**
     * Archives all documents for a booking (called when booking is cancelled).
     */
    void archiveDocumentsForBooking(Long bookingId);

    /**
     * Returns all documents for a specific booking (including archived). For admin use.
     */
    List<BookingDocumentResponse> getForBooking(Long bookingId);
}
