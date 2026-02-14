package ru.savvy.soldo.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.dto.TelegramAuthRequest;
import ru.savvy.soldo.enums.UserRole;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.exception.RoleAlreadyExistsException;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.repository.UserRepository;
import ru.savvy.soldo.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public User findOrCreateByTelegramId(TelegramAuthRequest request) {
        return repository.findByTelegramId(request.getTelegramId())
                .map(existing -> {
                    // Обновляем профиль — пользователь мог сменить имя в TG
                    existing.setFirstName(request.getFirstName());
                    existing.setLastName(request.getLastName());
                    existing.setUsername(request.getUsername());
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
                    return repository.save(newUser);
                });
    }

    @Override
    public User findByTelegramId(Long telegramId) {
        return repository.findByTelegramId(telegramId)
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