package ru.savvy.soldo.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.auth.dto.AdminLoginRequest;
import ru.savvy.soldo.auth.dto.TokenResponse;
import ru.savvy.soldo.user.model.User;
import ru.savvy.soldo.shared.security.JwtTokenProvider;
import ru.savvy.soldo.user.service.UserService;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }

        if (!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).build();
        }

        String token = jwtTokenProvider.generateToken(
                String.valueOf(user.getId()), user.getRole());

        return ResponseEntity.ok(new TokenResponse(
                "Bearer " + token, user.getRole(), user.getId()));
    }
}