package ru.savvy.soldo.event.mapper;

import org.mapstruct.*;
import ru.savvy.soldo.event.dto.EventDTO;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.event.model.EventStatus;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "status", source = "status")
    @Mapping(target = "id", source = "id")
    // availableSpots is computed in EventService, not mapped here
    @Mapping(target = "availableSpots", ignore = true)
    EventDTO entityToDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Event dtoToEntity(EventDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(EventDTO dto, @MappingTarget Event event);

    default String mapEventStatus(EventStatus status) {
        return status != null ? status.name() : null;
    }

    default EventStatus mapToEventStatus(String status) {
        return status != null ? EventStatus.valueOf(status) : null;
    }
}
