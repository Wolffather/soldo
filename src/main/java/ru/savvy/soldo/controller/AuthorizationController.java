package ru.savvy.soldo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.request.TelegramAuthRequest;
import ru.savvy.soldo.dto.response.TokenResponse;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.security.JwtTokenProvider;
import ru.savvy.soldo.service.UserService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final String botSecret;

    public AuthorizationController(UserService userService,
                                   JwtTokenProvider jwtTokenProvider,
                                   @Value("${telegram.bot.secret}") String botSecret) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.botSecret = botSecret;
    }

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
                user.getId().toString(), user.getRole());

        return ResponseEntity.ok(
                new TokenResponse("Bearer " + token, user.getRole(), user.getId()));
    }
}