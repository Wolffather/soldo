package ru.savvy.soldo.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.booking.model.BookingDocument;
import ru.savvy.soldo.document.model.DocumentTemplate;
import ru.savvy.soldo.booking.repository.BookingDocumentRepository;
import ru.savvy.soldo.document.repository.DocumentTemplateRepository;
import ru.savvy.soldo.booking.service.BookingDocumentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingDocumentServiceImpl implements BookingDocumentService {

    private final DocumentTemplateRepository documentTemplateRepository;
    private final BookingDocumentRepository bookingDocumentRepository;

    @Override
    public void createDocumentsForBooking(Booking booking, String categoryFormat) {
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
    }
}
