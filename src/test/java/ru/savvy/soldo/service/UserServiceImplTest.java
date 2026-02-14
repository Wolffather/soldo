package ru.savvy.soldo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.savvy.soldo.dto.TelegramAuthRequest;
import ru.savvy.soldo.enums.UserRole;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.exception.RoleAlreadyExistsException;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.repository.UserRepository;
import ru.savvy.soldo.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    private User createUser(Long id, Long telegramId, String role) {
        return User.builder()
                .id(id)
                .telegramId(telegramId)
                .firstName("Test")
                .lastName("User")
                .username("testuser")
                .role(role)
                .build();
    }

    private TelegramAuthRequest createAuthRequest(Long telegramId) {
        TelegramAuthRequest request = new TelegramAuthRequest();
        request.setTelegramId(telegramId);
        request.setFirstName("Test");
        request.setLastName("User");
        request.setUsername("testuser");
        return request;
    }

    // ─── findOrCreateByTelegramId ──────────────────────────

    @Test
    @DisplayName("Существующий пользователь — обновляет профиль")
    void findOrCreate_existingUser_updatesProfile() {
        User existing = createUser(1L, 123L, "USER");
        when(repository.findByTelegramId(123L)).thenReturn(Optional.of(existing));
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        TelegramAuthRequest request = createAuthRequest(123L);
        request.setFirstName("Updated");

        User result = service.findOrCreateByTelegramId(request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
        verify(repository).save(existing);
        verify(repository, never()).save(argThat(u -> u.getId() == null));
    }

    @Test
    @DisplayName("Новый пользователь — создаёт с ролью USER")
    void findOrCreate_newUser_createsWithUserRole() {
        when(repository.findByTelegramId(456L)).thenReturn(Optional.empty());
        when(repository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        TelegramAuthRequest request = createAuthRequest(456L);

        User result = service.findOrCreateByTelegramId(request);

        assertThat(result.getTelegramId()).isEqualTo(456L);
        assertThat(result.getRole()).isEqualTo("USER");
        verify(repository).save(any(User.class));
    }

    // ─── findByTelegramId ─────────────────────────────────

    @Test
    @DisplayName("Поиск по telegramId — найден")
    void findByTelegramId_found() {
        User user = createUser(1L, 123L, "USER");
        when(repository.findByTelegramId(123L)).thenReturn(Optional.of(user));

        User result = service.findByTelegramId(123L);

        assertThat(result.getTelegramId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("Поиск по telegramId — не найден")
    void findByTelegramId_notFound_throwsException() {
        when(repository.findByTelegramId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByTelegramId(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
    }

    // ─── findById ─────────────────────────────────────────

    @Test
    @DisplayName("Поиск по id — не найден")
    void findById_notFound_throwsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── grantAdminRole ───────────────────────────────────

    @Test
    @DisplayName("Выдача роли ADMIN — успешно")
    void grantAdminRole_success() {
        User user = createUser(1L, 123L, "USER");
        when(repository.findByTelegramId(123L)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.grantAdminRole(123L);

        assertThat(result.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Выдача роли ADMIN — уже админ")
    void grantAdminRole_alreadyAdmin_throwsException() {
        User user = createUser(1L, 123L, "ADMIN");
        when(repository.findByTelegramId(123L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.grantAdminRole(123L))
                .isInstanceOf(RoleAlreadyExistsException.class);
    }

    // ─── getAllUsers ──────────────────────────────────────

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers_returnsList() {
        List<User> users = List.of(
                createUser(1L, 123L, "USER"),
                createUser(2L, 456L, "ADMIN"));
        when(repository.findAll()).thenReturn(users);

        List<User> result = service.getAllUsers();

        assertThat(result).hasSize(2);
    }
}