package ru.savvy.soldo.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.savvy.soldo.booking.dto.BookingDTO;
import ru.savvy.soldo.booking.dto.BookingResponse;
import ru.savvy.soldo.booking.model.BookingStatus;
import ru.savvy.soldo.booking.model.Booking;
import ru.savvy.soldo.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring", imports = {BookingStatus.class})
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "event", source = "eventId", qualifiedByName = "eventFromId")
    @Mapping(target = "status", source = "status", defaultExpression = "java(BookingStatus.PENDING)")
    @Mapping(target = "createdAt", ignore = true)
    Booking dtoToEntity(BookingDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    BookingResponse entityToResponse(Booking booking);

    List<BookingResponse> entitiesToResponses(List<Booking> bookings);

    @Named("eventFromId")
    default Event eventFromId(Long eventId) {
        if (eventId == null) return null;
        return Event.builder()
                .id(eventId)
                .build();
    }
}