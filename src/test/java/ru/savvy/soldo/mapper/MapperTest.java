package ru.savvy.soldo.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.savvy.soldo.dto.BookingDTO;
import ru.savvy.soldo.dto.BookingResponse;
import ru.savvy.soldo.enums.BookingStatus;
import ru.savvy.soldo.model.Booking;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.User;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BookingMapperTest {

    @Autowired
    private BookingMapper mapper;

    @Test
    @DisplayName("DTO → Entity: eventId маппится в Event")
    void dtoToEntity_mapsEventId() {
        BookingDTO dto = BookingDTO.builder()
                .eventId(5L)
                .status(BookingStatus.PENDING)
                .build();

        Booking booking = mapper.dtoToEntity(dto);

        assertThat(booking.getEvent().getId()).isEqualTo(5L);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getUser()).isNull(); // устанавливается в контроллере
        assertThat(booking.getId()).isNull();
    }

    @Test
    @DisplayName("DTO → Entity: статус по умолчанию PENDING")
    void dtoToEntity_defaultStatusPending() {
        BookingDTO dto = BookingDTO.builder()
                .eventId(1L)
                .build(); // status = null

        Booking booking = mapper.dtoToEntity(dto);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    @DisplayName("Entity → Response: маппит вложенные поля")
    void entityToResponse_mapsNestedFields() {
        Booking booking = Booking.builder()
                .id(1L)
                .user(User.builder().id(10L).build())
                .event(Event.builder().id(20L).title("Concert").build())
                .status(BookingStatus.CONFIRMED)
                .build();

        BookingResponse response = mapper.entityToResponse(booking);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(10L);
        assertThat(response.getEventId()).isEqualTo(20L);
        assertThat(response.getEventTitle()).isEqualTo("Concert");
        assertThat(response.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }
}