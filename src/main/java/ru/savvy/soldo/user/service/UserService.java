package ru.savvy.soldo.user.service;

import ru.savvy.soldo.user.dto.UserRegisterRequest;
import ru.savvy.soldo.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findById(Long id);
    List<User> getAllUsers();
    User grantAdminRoleById(Long id);
    Optional<User> findByUsername(String username);
    User registerUser(UserRegisterRequest request);
}
