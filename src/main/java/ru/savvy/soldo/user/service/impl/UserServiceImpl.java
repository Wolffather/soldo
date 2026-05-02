package ru.savvy.soldo.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.user.dto.UserRegisterRequest;
import ru.savvy.soldo.user.model.UserRole;
import ru.savvy.soldo.shared.exception.NotFoundException;
import ru.savvy.soldo.user.model.User;
import ru.savvy.soldo.user.repository.UserRepository;
import ru.savvy.soldo.tenant.TenantContext;
import ru.savvy.soldo.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
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
    public User registerUser(UserRegisterRequest request) {
        Long tenantId = TenantContext.getCurrentTenantId() != null
                ? TenantContext.getCurrentTenantId() : 1L;
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(UserRole.ADMIN.name())
                .tenantId(tenantId)
                .build();
        return repository.save(user);
    }
}
