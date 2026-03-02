package ru.savvy.soldo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.dto.request.TelegramAuthRequest;
import ru.savvy.soldo.dto.request.UserRegisterRequest;
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
    private final PasswordEncoder passwordEncoder;

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
                    // telegram_id column removed — no longer setting it
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
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

    @Override
    @Transactional
    public User grantAdminRoleById(Long id) {
        User user = findById(id);
        if (user.hasRole(UserRole.ADMIN)) {
            throw new RoleAlreadyExistsException(
                    "Уже выданы права админа пользователю с id=" + id);
        }
        user.setRole(UserRole.ADMIN.name());
        return repository.save(user);
    }

    @Override
    @Transactional
    public User findOrCreateByOAuth(AuthProviderType provider, String providerUserId,
                                     String firstName, String lastName, String username) {
        // 1. Look up existing auth provider entry
        return authProviderRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .map(UserAuthProvider::getUser)
                .orElseGet(() -> {
                    // 2. Try to find user by name+username for cross-provider deduplication
                    User user = null;
                    if (username != null && !username.isBlank()
                            && firstName != null && lastName != null) {
                        user = repository
                                .findByFirstNameAndLastNameAndUsername(firstName, lastName, username)
                                .orElse(null);
                    }

                    // 3. Create new user if still not found
                    if (user == null) {
                        user = repository.save(User.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .username(username)
                                .role(UserRole.USER.name())
                                .build());
                    }

                    // 4. Register this OAuth provider for the user
                    authProviderRepository.save(UserAuthProvider.builder()
                            .user(user)
                            .provider(provider)
                            .providerUserId(providerUserId)
                            .build());

                    return user;
                });
    }

    @Override
    @Transactional
    public User registerUser(UserRegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(UserRole.USER.name())
                .build();
        return repository.save(user);
    }
}
