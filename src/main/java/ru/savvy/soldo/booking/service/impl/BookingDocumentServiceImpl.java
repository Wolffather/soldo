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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingDocumentServiceImpl implements BookingDocumentService {

    private final DocumentTemplateRepository documentTemplateRepository;
    private final BookingDocumentRepository bookingDocumentRepository;

    @Override
    public void createDocumentsForBooking(Booking booking, String categoryFormat) {
        // Не создаём дубликаты — если уже есть документы для этого бронирования, пропускаем
        List<BookingDocument> existing = bookingDocumentRepository.findByBookingId(booking.getId());
        if (!existing.isEmpty()) return;

        List<DocumentTemplate> templates = documentTemplateRepository.findByCategoryFormat(categoryFormat);
        List<BookingDocument> docs = templates.stream()
                .map(template -> {
                    BookingDocument doc = new BookingDocument();
                    doc.setBooking(booking);
                    doc.setDocumentTemplate(template);
                    return doc;
                })
                .toList();
        bookingDocumentRepository.saveAll(docs);
        log.info("Создано {} документов для бронирования {}", docs.size(), booking.getId());
    }

    @Override
    @Transactional
    public List<BookingDocumentResponse> getForBooking(Long bookingId) {
        List<BookingDocument> docs = bookingDocumentRepository.findByBookingId(bookingId);
        return docs.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void archiveDocumentsForBooking(Long bookingId) {
        List<BookingDocument> docs = bookingDocumentRepository.findByBookingId(bookingId);
        if (docs.isEmpty()) return;
        docs.forEach(d -> d.setArchived(true));
        bookingDocumentRepository.saveAll(docs);
        log.info("Архивированы {} документов для бронирования {}", docs.size(), bookingId);
    }

    private BookingDocumentResponse toResponse(BookingDocument doc) {
        DocumentTemplate tmpl = doc.getDocumentTemplate();
        Booking booking = doc.getBooking();
        String eventTitle = booking.getEvent() != null ? booking.getEvent().getTitle() : null;

        return BookingDocumentResponse.builder()
                .id(doc.getId())
                .bookingId(booking.getId())
                .eventTitle(eventTitle)
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
                .build();
    }
}
