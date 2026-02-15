package ru.savvy.soldo.service;

import ru.savvy.soldo.dto.request.TelegramAuthRequest;
import ru.savvy.soldo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findOrCreateByTelegramId(TelegramAuthRequest request);
    User findByTelegramId(Long telegramId);
    User findById(Long id);
    List<User> getAllUsers();
    User grantAdminRole(Long telegramId);
    Optional<User> findByUsername(String username);

}