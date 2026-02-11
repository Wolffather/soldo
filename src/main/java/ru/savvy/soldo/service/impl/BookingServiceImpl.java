package ru.savvy.soldo.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.enums.BookingStatus;
import ru.savvy.soldo.exception.IllegalOperationException;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.repository.BookingRepository;
import ru.savvy.soldo.service.BookingService;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.repository = bookingRepository;
    }


    @Override
    public Booking findBookingById(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдена запись с bookingId=%s", bookingId)));
    }

    @Override
    public List<Booking> findAllUserBookings(Long userId) {
        return repository.
                findAllByUserId(userId).
                orElseThrow(() -> new NotFoundException(String.format("Не найдены записи для пользователя с userId=%s", userId)));
    }

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        Booking saved = repository.save(booking);
        BookingStatus status = saved.getStatus();
        if (status.equals(BookingStatus.CONFIRMED)) {
            handleSummaryOnCreateConfirmed(booking);
        } else handleSummaryOnCreatePending(booking);
        return saved;
    }

    @Override
    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = findBookingById(bookingId);

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = repository.save(booking);

        handleSummaryOnConfirm(booking);

        return saved;
    }

    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = findBookingById(bookingId);

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = repository.save(booking);

        handleSummaryOnCancel(booking);

        return saved;
    }

    @Override
    public void deleteBooking(Long bookingId) {
        Booking booking = findBookingById(bookingId);

        BookingStatus status = booking.getStatus();
        if (status != BookingStatus.CANCELLED) {
            throw new IllegalOperationException("Удаление доступно только для записей в статусе CANCELLED");
        } else {
            repository.delete(booking);
        }

    }

    private void handleSummaryOnCreateConfirmed(Booking booking) {
        Long eventId = booking.getEvent().getId();
        executeUpdateFunction("booking_on_create_confirmed.sql", eventId);
    }

    private void handleSummaryOnCreatePending(Booking booking) {
        Long eventId = booking.getEvent().getId();
        executeUpdateFunction("booking_on_create_pending.sql", eventId);
    }

    private void handleSummaryOnConfirm(Booking booking) {
        Long eventId = booking.getEvent().getId();
        executeUpdateFunction("booking_on_confirm.sql", eventId);
    }

    private void handleSummaryOnCancel(Booking booking) {
        Long eventId = booking.getEvent().getId();
        executeUpdateFunction("booking_on_cancel.sql", eventId);
    }

    private void executeUpdateFunction(String functionName, Long eventId) {
        em.createNativeQuery("SELECT " + functionName + "(:eventId)")
                .setParameter("eventId", eventId)
                .getSingleResult();
    }
}

