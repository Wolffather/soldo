package ru.savvy.soldo.service;

import ru.savvy.soldo.model.Booking;

public interface BookingDocumentService {

    /**
     * Creates document stubs (based on templates) for a camp-type booking.
     *
     * @param booking        the newly created booking
     * @param categoryFormat the format name of the event category (e.g. "SESSION_CITY")
     */
    void createDocumentsForBooking(Booking booking, String categoryFormat);
}
