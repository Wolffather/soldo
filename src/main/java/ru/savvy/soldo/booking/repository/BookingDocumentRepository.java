package ru.savvy.soldo.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.savvy.soldo.booking.model.BookingDocument;

import java.util.List;

public interface BookingDocumentRepository extends JpaRepository<BookingDocument, Long> {

    List<BookingDocument> findByBookingId(Long bookingId);

    List<BookingDocument> findByBookingIdIn(List<Long> bookingIds);

    /** Число активных (не архивных) документов для бронирования */
    @Query("SELECT COUNT(d) FROM BookingDocument d WHERE d.booking.id = :bookingId AND d.archived <> true")
    int countActiveByBookingId(@Param("bookingId") Long bookingId);

    /** Число подписанных активных документов */
    @Query("SELECT COUNT(d) FROM BookingDocument d WHERE d.booking.id = :bookingId AND d.archived <> true AND d.delivered = true")
    int countActiveSignedByBookingId(@Param("bookingId") Long bookingId);

    /** Число активных документов, требующих электронной подписи */
    @Query("SELECT COUNT(d) FROM BookingDocument d JOIN d.documentTemplate t " +
           "WHERE d.booking.id = :bookingId AND d.archived <> true AND t.requiresSignature = true")
    int countActiveRequiresSignatureByBookingId(@Param("bookingId") Long bookingId);
}
