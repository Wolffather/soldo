package ru.savvy.soldo.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.booking.model.BookingDocument;

import java.util.List;

public interface BookingDocumentRepository extends JpaRepository<BookingDocument, Long> {

    List<BookingDocument> findByBookingId(Long bookingId);
}
