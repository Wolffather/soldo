package ru.savvy.soldo.mapper;

import org.mapstruct.Mapper;
import ru.savvy.soldo.dto.response.UserResponse;
import ru.savvy.soldo.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse entityToResponse(User user);

    List<UserResponse> entitiesToResponses(List<User> users);
}