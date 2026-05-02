package ru.savvy.soldo.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.user.dto.UserResponse;
import ru.savvy.soldo.user.mapper.UserMapper;
import ru.savvy.soldo.user.service.UserService;

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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findUser(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.entityToResponse(service.findById(id)));
    }
}
