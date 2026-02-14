package ru.savvy.soldo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.EventBookingsSummary;
import ru.savvy.soldo.repository.EventBookingSummaryRepository;
import ru.savvy.soldo.repository.EventRepository;
import ru.savvy.soldo.service.impl.EventServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository repository;

    @Mock
    private EventBookingSummaryRepository summaryRepository;

    @InjectMocks
    private EventServiceImpl service;

    private Event testEvent() {
        return Event.builder()
                .id(1L)
                .title("Test")
                .type("Workshop")
                .price(BigDecimal.valueOf(100))
                .numOfParticipants(20)
                .build();
    }

    @Test
    @DisplayName("Сохранение события — создаёт summary")
    void saveEvent_createsSummary() {
        Event event = testEvent();
        when(repository.save(any())).thenReturn(event);
        when(summaryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Event result = service.saveEvent(event);

        assertThat(result.getTitle()).isEqualTo("Test");
        verify(summaryRepository).save(argThat(summary ->
                                                       summary.getAvailableSeats().equals(20)
                                                               && summary.getTotalBookings().equals(0)));
    }

    @Test
    @DisplayName("Получение всех событий")
    void getAllEvents_returnsList() {
        when(repository.findAll()).thenReturn(List.of(testEvent()));

        List<Event> result = service.getAllEvents();

        assertThat(result).hasSize(1);
    }
}