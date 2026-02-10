package ru.savvy.soldo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.savvy.soldo.dto.UserDTO;
import ru.savvy.soldo.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User dtoToEntity(UserDTO dto);

    UserDTO entityToDto(User user);
}
