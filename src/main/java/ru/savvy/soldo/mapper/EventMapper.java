package ru.savvy.soldo.mapper;

import org.mapstruct.*;
import ru.savvy.soldo.dto.EventDTO;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.enums.AgeGroup;
import ru.savvy.soldo.model.enums.EventStatus;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "ageGroup", source = "ageGroup")
    @Mapping(target = "id", source = "id")
    EventDTO entityToDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "ageGroup", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Event dtoToEntity(EventDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "ageGroup", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(EventDTO dto, @MappingTarget Event event);

    default String mapEventStatus(EventStatus status) {
        return status != null ? status.name() : null;
    }

    default EventStatus mapToEventStatus(String status) {
        return status != null ? EventStatus.valueOf(status) : null;
    }

    default String mapAgeGroup(AgeGroup ageGroup) {
        return ageGroup != null ? ageGroup.name() : null;
    }

    default AgeGroup mapToAgeGroup(String ageGroup) {
        return ageGroup != null ? AgeGroup.valueOf(ageGroup) : null;
    }
}