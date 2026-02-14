package ru.savvy.soldo.service;

import ru.savvy.soldo.dto.TelegramAuthRequest;
import ru.savvy.soldo.model.User;

import java.util.List;

public interface UserService {
    User findOrCreateByTelegramId(TelegramAuthRequest request);
    User findByTelegramId(Long telegramId);
    User findById(Long id);
    List<User> getAllUsers();
    User grantAdminRole(Long telegramId);
}