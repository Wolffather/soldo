package ru.savvy.soldo.mapper;

import org.mapstruct.*;
import ru.savvy.soldo.dto.SeasonDTO;
import ru.savvy.soldo.model.Season;
import ru.savvy.soldo.model.enums.EventStatus;

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
