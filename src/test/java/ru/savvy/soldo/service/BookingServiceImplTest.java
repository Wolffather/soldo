package ru.savvy.soldo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.savvy.soldo.enums.BookingStatus;
import ru.savvy.soldo.exception.IllegalOperationException;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.EventBookingsSummary;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.repository.BookingRepository;
import ru.savvy.soldo.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.service.impl.BookingServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventBookingSummaryRepository summaryRepository;

    @InjectMocks
    private BookingServiceImpl service;

    private User testUser() {
        return User.builder().id(1L).telegramId(123L).role("USER").build();
    }

    private Event testEvent() {
        return Event.builder().id(1L).title("Test Event").numOfParticipants(10).build();
    }

    private Booking testBooking(BookingStatus status) {
        return Booking.builder()
                .id(1L)
                .user(testUser())
                .event(testEvent())
                .status(status)
                .build();
    }

    private EventBookingsSummary testSummary(int available) {
        return EventBookingsSummary.builder()
                .event(testEvent())
                .totalBookings(5)
                .confirmedBookings(3)
                .availableSeats(available)
                .build();
    }

    // ─── findBookingById ──────────────────────────────────

    @Nested
    @DisplayName("findBookingById")
    class FindById {

        @Test
        @DisplayName("Найден")
        void found() {
            Booking booking = testBooking(BookingStatus.PENDING);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            Booking result = service.findBookingById(1L);

            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Не найден — NotFoundException")
        void notFound() {
            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findBookingById(999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ─── createBooking ────────────────────────────────────

    @Nested
    @DisplayName("createBooking")
    class Create {

        @Test
        @DisplayName("Создание PENDING — успешно")
        void createPending_success() {
            Booking booking = testBooking(BookingStatus.PENDING);
            when(bookingRepository.existsActiveBooking(1L, 1L)).thenReturn(false);
            when(bookingRepository.save(any())).thenReturn(booking);

            Booking result = service.createBooking(booking);

            assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);
            verify(summaryRepository).onCreatePending(1L);
            verify(summaryRepository, never()).onCreateConfirmed(any());
        }

        @Test
        @DisplayName("Создание CONFIRMED — обновляет confirmed + available")
        void createConfirmed_success() {
            Booking booking = testBooking(BookingStatus.CONFIRMED);
            when(bookingRepository.existsActiveBooking(1L, 1L)).thenReturn(false);
            when(bookingRepository.save(any())).thenReturn(booking);
            when(summaryRepository.findById(1L))
                    .thenReturn(Optional.of(testSummary(5)));

            Booking result = service.createBooking(booking);

            assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
            verify(summaryRepository).onCreateConfirmed(1L);
        }

        @Test
        @DisplayName("Дубликат бронирования — исключение")
        void duplicate_throwsException() {
            Booking booking = testBooking(BookingStatus.PENDING);
            when(bookingRepository.existsActiveBooking(1L, 1L)).thenReturn(true);

            assertThatThrownBy(() -> service.createBooking(booking))
                    .isInstanceOf(IllegalOperationException.class)
                    .hasMessageContaining("уже есть активное");
        }

        @Test
        @DisplayName("Нет мест при CONFIRMED — исключение")
        void noSeats_throwsException() {
            Booking booking = testBooking(BookingStatus.CONFIRMED);
            when(bookingRepository.existsActiveBooking(1L, 1L)).thenReturn(false);
            when(summaryRepository.findById(1L))
                    .thenReturn(Optional.of(testSummary(0)));

            assertThatThrownBy(() -> service.createBooking(booking))
                    .isInstanceOf(IllegalOperationException.class)
                    .hasMessageContaining("Нет свободных мест");
        }
    }

    // ─── confirmBooking ───────────────────────────────────

    @Nested
    @DisplayName("confirmBooking")
    class Confirm {

        @Test
        @DisplayName("PENDING → CONFIRMED — успешно")
        void pendingToConfirmed_success() {
            Booking booking = testBooking(BookingStatus.PENDING);
            when(summaryRepository.findById(1L))
                    .thenReturn(Optional.of(testSummary(5)));
            when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Booking result = service.confirmBooking(booking);

            assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
            verify(summaryRepository).onConfirm(1L);
        }

        @Test
        @DisplayName("CONFIRMED → CONFIRMED — исключение")
        void alreadyConfirmed_throwsException() {
            Booking booking = testBooking(BookingStatus.CONFIRMED);

            assertThatThrownBy(() -> service.confirmBooking(booking))
                    .isInstanceOf(IllegalOperationException.class)
                    .hasMessageContaining("уже в статусе");
        }

        @Test
        @DisplayName("CANCELLED → CONFIRMED — исключение")
        void cancelledToConfirmed_throwsException() {
            Booking booking = testBooking(BookingStatus.CANCELLED);

            assertThatThrownBy(() -> service.confirmBooking(booking))
                    .isInstanceOf(IllegalOperationException.class)
                    .hasMessageContaining("отменённого");
        }

        @Test
        @DisplayName("Нет мест — исключение")
        void noSeats_throwsException() {
            Booking booking = testBooking(BookingStatus.PENDING);
            when(summaryRepository.findById(1L))
                    .thenReturn(Optional.of(testSummary(0)));

            assertThatThrownBy(() -> service.confirmBooking(booking))
                    .isInstanceOf(IllegalOperationException.class)
                    .hasMessageContaining("Нет свободных мест");
        }
    }

    // ─── cancelBooking ────────────────────────────────────

    @Nested
    @DisplayName("cancelBooking")
    class Cancel {

        @Test
        @DisplayName("CONFIRMED → CANCELLED — обновляет confirmed")
        void confirmedToCancelled() {
            Booking booking = testBooking(BookingStatus.CONFIRMED);
            when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Booking result = service.cancelBooking(booking);

            assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
            verify(summaryRepository).onCancelFromConfirmed(1L);
            verify(summaryRepository, never()).onCancelFromPending(any());
        }

        @Test
        @DisplayName("PENDING → CANCELLED — обновляет pending")
        void pendingToCancelled() {
            Booking booking = testBooking(BookingStatus.PENDING);
            when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Booking result = service.cancelBooking(booking);

            assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
            verify(summaryRepository).onCancelFromPending(1L);
            verify(summaryRepository, never()).onCancelFromConfirmed(any());
        }

        @Test
        @DisplayName("CANCELLED → CANCELLED — исключение")
        void alreadyCancelled_throwsException() {
            Booking booking = testBooking(BookingStatus.CANCELLED);

            assertThatThrownBy(() -> service.cancelBooking(booking))
                    .isInstanceOf(IllegalOperationException.class);
        }
    }

    // ─── deleteBooking ────────────────────────────────────

    @Nested
    @DisplayName("deleteBooking")
    class Delete {

        @Test
        @DisplayName("CANCELLED — удаляет и обновляет summary")
        void cancelled_deletesSuccessfully() {
            Booking booking = testBooking(BookingStatus.CANCELLED);

            service.deleteBooking(booking);

            verify(bookingRepository).delete(booking);
            verify(summaryRepository).onDelete(1L);
        }

        @Test
        @DisplayName("PENDING — исключение")
        void pending_throwsException() {
            Booking booking = testBooking(BookingStatus.PENDING);

            assertThatThrownBy(() -> service.deleteBooking(booking))
                    .isInstanceOf(IllegalOperationException.class)
                    .hasMessageContaining("CANCELLED");
        }

        @Test
        @DisplayName("CONFIRMED — исключение")
        void confirmed_throwsException() {
            Booking booking = testBooking(BookingStatus.CONFIRMED);

            assertThatThrownBy(() -> service.deleteBooking(booking))
                    .isInstanceOf(IllegalOperationException.class);
        }
    }
}