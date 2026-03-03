package ru.savvy.soldo.user.service;

import ru.savvy.soldo.user.dto.TelegramAuthRequest;
import ru.savvy.soldo.user.dto.UserRegisterRequest;
import ru.savvy.soldo.user.model.User;
import ru.savvy.soldo.user.model.AuthProviderType;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findOrCreateByTelegramId(TelegramAuthRequest request);
    User findByTelegramId(Long telegramId);
    User findById(Long id);
    List<User> getAllUsers();
    User grantAdminRole(Long telegramId);
    User grantAdminRoleById(Long id);
    Optional<User> findByUsername(String username);

    /** Register a new user with login + password. */
    User registerUser(UserRegisterRequest request);

    /**
     * Find or create a user by OAuth provider (VK, Yandex, etc.).
     * 1. Looks up user_auth_providers by (provider, providerUserId).
     * 2. If not found, tries to match by (firstName, lastName, username) for cross-provider dedup.
     * 3. If still not found — creates a new user and registers the auth provider.
     */
    User findOrCreateByOAuth(AuthProviderType provider, String providerUserId,
                              String firstName, String lastName, String username);
}
