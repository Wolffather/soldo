package ru.savvy.soldo.booking.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.booking.dto.BookingDocumentResponse;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.booking.model.BookingDocument;
import ru.savvy.soldo.booking.repository.BookingDocumentRepository;
import ru.savvy.soldo.document.model.DocumentTemplate;
import ru.savvy.soldo.document.repository.DocumentTemplateRepository;
import ru.savvy.soldo.booking.service.BookingDocumentService;
import ru.savvy.soldo.shared.email.EmailService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingDocumentServiceImpl implements BookingDocumentService {

    private final DocumentTemplateRepository documentTemplateRepository;
    private final BookingDocumentRepository bookingDocumentRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void createDocumentsForBooking(Booking booking) {
        if (bookingDocumentRepository.findByBookingId(booking.getId()).isEmpty()) {
            Long eventId = booking.getEvent().getId();
            List<DocumentTemplate> templates = documentTemplateRepository.findByEventId(eventId);
            if (!templates.isEmpty()) {
                List<BookingDocument> docs = templates.stream()
                        .map(t -> BookingDocument.builder().booking(booking).documentTemplate(t).build())
                        .toList();
                bookingDocumentRepository.saveAll(docs);
                log.info("Created {} documents for booking {}", docs.size(), booking.getId());
            }
        }
    }

    @Override
    @Transactional
    public void sendDocumentEmail(Booking booking) {
        List<BookingDocument> docs = bookingDocumentRepository.findByBookingId(booking.getId())
                .stream().filter(d -> !Boolean.TRUE.equals(d.getArchived())).toList();

        emailService.sendDocuments(booking, docs);

        LocalDateTime now = LocalDateTime.now();
        docs.forEach(d -> d.setEmailSentAt(now));
        bookingDocumentRepository.saveAll(docs);
    }

    @Override
    @Transactional
    public void archiveDocumentsForBooking(Long bookingId) {
        List<BookingDocument> docs = bookingDocumentRepository.findByBookingId(bookingId);
        if (docs.isEmpty()) return;
        docs.forEach(d -> d.setArchived(true));
        bookingDocumentRepository.saveAll(docs);
        log.info("Archived {} documents for booking {}", docs.size(), bookingId);
    }

    @Override
    @Transactional
    public List<BookingDocumentResponse> getForBooking(Long bookingId) {
        return bookingDocumentRepository.findByBookingId(bookingId).stream()
                .map(this::toResponse)
                .toList();
    }

    private BookingDocumentResponse toResponse(BookingDocument doc) {
        DocumentTemplate tmpl = doc.getDocumentTemplate();
        Booking booking = doc.getBooking();
        return BookingDocumentResponse.builder()
                .id(doc.getId())
                .bookingId(booking.getId())
                .eventTitle(booking.getEvent() != null ? booking.getEvent().getTitle() : null)
                .templateId(tmpl != null ? tmpl.getId() : null)
                .templateName(tmpl != null ? tmpl.getName() : null)
                .templateDescription(tmpl != null ? tmpl.getDescription() : null)
                .templateFileUrl(tmpl != null ? tmpl.getFileUrl() : null)
                .templateIsRequired(tmpl != null ? tmpl.getIsRequired() : null)
                .templateCategoryFormat(tmpl != null ? tmpl.getCategoryFormat() : null)
                .templateRequiresSignature(tmpl != null ? tmpl.getRequiresSignature() : null)
                .delivered(doc.getDelivered())
                .deliveredAt(doc.getDeliveredAt())
                .archived(doc.getArchived())
                .signerName(doc.getSignerName())
                .signedAt(doc.getSignedAt())
                .filledData(doc.getFilledData())
                .emailSentAt(doc.getEmailSentAt())
                .build();
    }
}
