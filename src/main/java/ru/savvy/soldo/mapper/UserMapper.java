package ru.savvy.soldo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.savvy.soldo.dto.response.UserResponse;
import ru.savvy.soldo.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "mapDateTime")
    UserResponse entityToResponse(User user);

    List<UserResponse> entitiesToResponses(List<User> users);

    @Named("mapDateTime")
    default String mapDateTime(LocalDateTime dt) {
        return dt != null ? dt.toString() : null;
    }
}
