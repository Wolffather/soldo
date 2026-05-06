package ru.savvy.soldo.shared.email;

import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.booking.model.BookingDocument;

import java.util.List;

public interface EmailService {

    /**
     * Sends an HTML email to the booking guest with links to their documents.
     * Does nothing if mail is disabled or guest has no email.
     */
    void sendDocuments(Booking booking, List<BookingDocument> documents);
}
