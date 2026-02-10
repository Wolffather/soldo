package ru.savvy.soldo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.savvy.soldo.dto.EventDTO;
import ru.savvy.soldo.model.Event;


@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    Event dtoToEntity(EventDTO dto);

    EventDTO entityToDto(Event event);
}

