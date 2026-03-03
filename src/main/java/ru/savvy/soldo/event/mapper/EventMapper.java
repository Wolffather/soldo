package ru.savvy.soldo.event.mapper;

import org.mapstruct.*;
import ru.savvy.soldo.event.dto.EventDTO;
import ru.savvy.soldo.event.model.Event;
import ru.savvy.soldo.event.model.EventStatus;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "seasonId", source = "season.id")
    @Mapping(target = "seasonTitle", source = "season.title")
    // availableSpots is computed in EventService, not mapped here
    @Mapping(target = "availableSpots", ignore = true)
    EventDTO entityToDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "season", ignore = true)   // resolved by service using seasonId
    Event dtoToEntity(EventDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "season", ignore = true)   // resolved by service using seasonId
    void updateEntityFromDto(EventDTO dto, @MappingTarget Event event);

    default String mapEventStatus(EventStatus status) {
        return status != null ? status.name() : null;
    }

    default EventStatus mapToEventStatus(String status) {
        return status != null ? EventStatus.valueOf(status) : null;
    }
}
