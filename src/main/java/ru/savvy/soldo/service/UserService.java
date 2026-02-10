package ru.savvy.soldo.service;

import ru.savvy.soldo.model.User;

import java.util.List;

public interface UserService {

    User grantAdminRole(String username);

    User createUser(User user);

    User findByUsername(String username);

    List<User> getAllUsers();

}
