package ru.savvy.soldo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.response.UserResponse;
import ru.savvy.soldo.mapper.UserMapper;
import ru.savvy.soldo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                mapper.entitiesToResponses(service.getAllUsers()));
    }

    @GetMapping("/{telegramId}")
    public ResponseEntity<UserResponse> findUser(
            @PathVariable Long telegramId) {
        return ResponseEntity.ok(
                mapper.entityToResponse(service.findByTelegramId(telegramId)));
    }

    /** Выдать роль ADMIN по Telegram ID (устарело — использовать /{id}/role). */
    @PatchMapping("/grant-admin-role/{telegramId}")
    public ResponseEntity<UserResponse> grantAdminRole(
            @PathVariable Long telegramId) {
        return ResponseEntity.ok(
                mapper.entityToResponse(service.grantAdminRole(telegramId)));
    }

    /** Выдать роль ADMIN по внутреннему ID пользователя. */
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> grantAdminRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.entityToResponse(service.grantAdminRoleById(id)));
    }
}
