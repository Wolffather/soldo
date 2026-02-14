package ru.savvy.soldo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.savvy.soldo.dto.TelegramAuthRequest;
import ru.savvy.soldo.enums.BookingStatus;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.service.BookingService;
import ru.savvy.soldo.service.EventService;
import ru.savvy.soldo.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Testcontainers
class BookingIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("soldo_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("jwt.secret",
                     () -> "test-secret-key-minimum-32-characters-long");
        registry.add("telegram.bot.secret", () -> "test-bot-secret");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EventRepository eventRepository;

    private User createTestUser(Long telegramId) {
        TelegramAuthRequest request = new TelegramAuthRequest();
        request.setTelegramId(telegramId);
        request.setFirstName("Test");
        return userService.findOrCreateByTelegramId(request);
    }

    private Event createTestEvent() {
        Event event = Event.builder()
                .title("Test Event")
                .type("Workshop")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .price(BigDecimal.valueOf(100))
                .numOfParticipants(10)
                .description("Test")
                .build();
        return eventService.saveEvent(event);
    }

    @Test
    @DisplayName("Полный цикл: создание → подтверждение → отмена → удаление")
    void fullBookingLifecycle() {
        User user = createTestUser(100L);
        Event event = createTestEvent();

        // Создание
        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .status(BookingStatus.PENDING)
                .build();
        Booking created = bookingService.createBooking(booking);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BookingStatus.PENDING);

        // Подтверждение
        Booking confirmed = bookingService.confirmBooking(created);
        assertThat(confirmed.getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        // Отмена
        Booking cancelled = bookingService.cancelBooking(confirmed);
        assertThat(cancelled.getStatus()).isEqualTo(BookingStatus.CANCELLED);

        // Удаление
        bookingService.deleteBooking(cancelled);
        assertThatThrownBy(() -> bookingService.findBookingById(cancelled.getId()))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Нельзя создать дубликат бронирования")
    void cannotCreateDuplicateBooking() {
        User user = createTestUser(200L);
        Event event = createTestEvent();

        Booking first = Booking.builder()
                .user(user)
                .event(event)
                .status(BookingStatus.PENDING)
                .build();
        bookingService.createBooking(first);

        Booking duplicate = Booking.builder()
                .user(user)
                .event(event)
                .status(BookingStatus.PENDING)
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(duplicate))
                .hasMessageContaining("уже есть активное");
    }
}