package ru.savvy.soldo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.savvy.soldo.dto.BookingDTO;
import ru.savvy.soldo.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    Booking dtoToEntity(BookingDTO dto);

    BookingDTO entityToDto(Booking booking);
}
