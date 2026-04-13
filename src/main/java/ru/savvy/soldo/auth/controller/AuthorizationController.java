package ru.savvy.soldo.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.user.dto.TelegramAuthRequest;
import ru.savvy.soldo.user.dto.UserLoginRequest;
import ru.savvy.soldo.user.dto.UserRegisterRequest;
import ru.savvy.soldo.auth.dto.TokenResponse;
import ru.savvy.soldo.user.model.User;
import ru.savvy.soldo.shared.security.JwtTokenProvider;
import ru.savvy.soldo.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${telegram.bot.secret}")
    private String botSecret;

    // ─── Telegram (используется ботом) ─────────────────────────────────────────

    @PostMapping("/telegram")
    public ResponseEntity<TokenResponse> authenticateViaTelegram(
            @Valid @RequestBody TelegramAuthRequest request,
            @RequestHeader("X-Bot-Secret") String secret) {

        if (!MessageDigest.isEqual(
                secret.getBytes(StandardCharsets.UTF_8),
                botSecret.getBytes(StandardCharsets.UTF_8))) {
            throw new AccessDeniedException("Неавторизованный клиент");
        }

        User user = userService.findOrCreateByTelegramId(request);

        String token = jwtTokenProvider.generateToken(
                user.getId().toString(), user.getRole(), user.getTenantId());

        return ResponseEntity.ok(
                new TokenResponse(token, user.getRole(), user.getId()));
    }

    // ─── Логин по логину + паролю ───────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody UserLoginRequest request) {
        User user = userService.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null
                || user.getPasswordHash() == null
                || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtTokenProvider.generateToken(
                user.getId().toString(), user.getRole(), user.getTenantId());

        return ResponseEntity.ok(new TokenResponse(token, user.getRole(), user.getId()));
    }

    // ─── Регистрация ────────────────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // 409 — логин занят
        }

        User user = userService.registerUser(request);

        String token = jwtTokenProvider.generateToken(
                user.getId().toString(), user.getRole(), user.getTenantId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TokenResponse(token, user.getRole(), user.getId()));
    }
}
