package ru.savvy.soldo.season.mapper;

import org.mapstruct.*;
import ru.savvy.soldo.season.dto.SeasonDTO;
import ru.savvy.soldo.season.model.Season;
import ru.savvy.soldo.event.model.EventStatus;

@Mapper(componentModel = "spring")
public interface SeasonMapper {

    @Mapping(target = "status", source = "status")
    @Mapping(target = "sessions", ignore = true)
    SeasonDTO entityToDto(Season season);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Season dtoToEntity(SeasonDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(SeasonDTO dto, @MappingTarget Season season);

    default String mapEventStatus(EventStatus status) {
        return status != null ? status.name() : null;
    }

    default EventStatus mapToEventStatus(String status) {
        return status != null ? EventStatus.valueOf(status) : null;
    }
}
