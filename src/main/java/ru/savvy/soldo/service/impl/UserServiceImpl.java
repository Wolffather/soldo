package ru.savvy.soldo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.dto.request.TelegramAuthRequest;
import ru.savvy.soldo.model.UserAuthProvider;
import ru.savvy.soldo.model.enums.AuthProviderType;
import ru.savvy.soldo.model.enums.UserRole;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.exception.RoleAlreadyExistsException;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.repository.UserAuthProviderRepository;
import ru.savvy.soldo.repository.UserRepository;
import ru.savvy.soldo.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserAuthProviderRepository authProviderRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    @Transactional
    public User findOrCreateByTelegramId(TelegramAuthRequest request) {
        String providerUserId = request.getTelegramId().toString();

        return authProviderRepository
                .findByProviderAndProviderUserId(AuthProviderType.TELEGRAM, providerUserId)
                .map(authProvider -> {
                    // Обновляем профиль — пользователь мог сменить имя в TG
                    User existing = authProvider.getUser();
                    existing.setFirstName(request.getFirstName());
                    existing.setLastName(request.getLastName());
                    existing.setUsername(request.getUsername());
                    existing.setTelegramId(request.getTelegramId());
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .telegramId(request.getTelegramId())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .username(request.getUsername())
                            .role(UserRole.USER.name())
                            .build();
                    newUser = repository.save(newUser);

                    UserAuthProvider authProvider = UserAuthProvider.builder()
                            .user(newUser)
                            .provider(AuthProviderType.TELEGRAM)
                            .providerUserId(providerUserId)
                            .build();
                    authProviderRepository.save(authProvider);

                    return newUser;
                });
    }

    @Override
    public User findByTelegramId(Long telegramId) {
        return authProviderRepository
                .findByProviderAndProviderUserId(AuthProviderType.TELEGRAM, telegramId.toString())
                .map(UserAuthProvider::getUser)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с telegramId=" + telegramId + " не найден"));
    }

    @Override
    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с id=" + id + " не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public User grantAdminRole(Long telegramId) {
        User user = findByTelegramId(telegramId);
        if (user.hasRole(UserRole.ADMIN)) {
            throw new RoleAlreadyExistsException(
                    "Уже выданы права админа пользователю с telegramId=" + telegramId);
        }
        user.setRole(UserRole.ADMIN.name());
        return repository.save(user);
    }
}