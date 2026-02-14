package ru.savvy.soldo.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.enums.BookingStatus;
import ru.savvy.soldo.exception.IllegalOperationException;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.repository.BookingRepository;
import ru.savvy.soldo.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.service.BookingService;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventBookingSummaryRepository summaryRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              EventBookingSummaryRepository summaryRepository) {
        this.bookingRepository = bookingRepository;
        this.summaryRepository = summaryRepository;
    }

    // ─── Чтение ────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        "Не найдена запись с bookingId=" + bookingId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findAllUserBookings(Long userId) {
        return bookingRepository.findAllByUserId(userId);
        // Пустой список — валидный результат, не бросаем NotFoundException
    }

    // ─── Создание ──────────────────────────────────────────

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        Long userId = booking.getUser().getId();
        Long eventId = booking.getEvent().getId();

        // 1. Проверяем дубликат бронирования
        if (bookingRepository.existsActiveBooking(userId, eventId)) {
            throw new IllegalOperationException(
                    "У пользователя уже есть активное бронирование на это событие");
        }

        // 2. Проверяем наличие мест (если статус сразу CONFIRMED)
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            validateAvailableSeats(eventId);
        }

        // 3. Сохраняем
        Booking saved = bookingRepository.save(booking);

        // 4. Обновляем summary
        if (saved.getStatus() == BookingStatus.CONFIRMED) {
            summaryRepository.onCreateConfirmed(eventId);
        } else {
            summaryRepository.onCreatePending(eventId);
        }

        return saved;
    }

    // ─── Подтверждение ─────────────────────────────────────

    @Override
    @Transactional
    public Booking confirmBooking(Booking booking) {
        // 1. Валидация перехода статуса
        validateStatusTransition(booking.getStatus(), BookingStatus.CONFIRMED);

        // 2. Проверяем наличие мест
        Long eventId = booking.getEvent().getId();
        validateAvailableSeats(eventId);

        // 3. Меняем статус
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        // 4. Обновляем summary
        summaryRepository.onConfirm(eventId);

        return saved;
    }

    // ─── Отмена ────────────────────────────────────────────

    @Override
    @Transactional
    public Booking cancelBooking(Booking booking) {
        BookingStatus previousStatus = booking.getStatus();

        // 1. Валидация перехода статуса
        validateStatusTransition(previousStatus, BookingStatus.CANCELLED);

        // 2. Меняем статус
        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        // 3. Обновляем summary в зависимости от предыдущего статуса
        Long eventId = booking.getEvent().getId();
        if (previousStatus == BookingStatus.CONFIRMED) {
            summaryRepository.onCancelFromConfirmed(eventId);
        } else {
            summaryRepository.onCancelFromPending(eventId);
        }

        return saved;
    }

    // ─── Удаление ──────────────────────────────────────────

    @Override
    @Transactional
    public void deleteBooking(Booking booking) {
        if (booking.getStatus() != BookingStatus.CANCELLED) {
            throw new IllegalOperationException(
                    "Удаление доступно только для записей в статусе CANCELLED");
        }
        bookingRepository.delete(booking);
    }

    // ─── Приватные методы ──────────────────────────────────

    /**
     * Валидация допустимых переходов между статусами.*
     *   PENDING   → CONFIRMED ✅
     *   PENDING   → CANCELLED ✅
     *   CONFIRMED → CANCELLED ✅
     *   CONFIRMED → CONFIRMED ❌
     *   CANCELLED → *         ❌
     */
    private void validateStatusTransition(BookingStatus from, BookingStatus to) {
        if (from == BookingStatus.CANCELLED) {
            throw new IllegalOperationException(
                    "Невозможно изменить статус отменённого бронирования");
        }
        if (from == to) {
            throw new IllegalOperationException(
                    "Бронирование уже в статусе " + to);
        }
        if (from == BookingStatus.CONFIRMED && to == BookingStatus.PENDING) {
            throw new IllegalOperationException(
                    "Невозможно вернуть подтверждённое бронирование в статус PENDING");
        }
    }

    private void validateAvailableSeats(Long eventId) {
        summaryRepository.findById(eventId).ifPresent(summary -> {
            if (summary.getAvailableSeats() <= 0) {
                throw new IllegalOperationException(
                        "Нет свободных мест на событие с eventId=" + eventId);
            }
        });
    }
}