package ru.savvy.soldo.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        // Минимум 32 символа для HS256
        provider = new JwtTokenProvider(
                "test-secret-key-minimum-32-characters-long", 86400000L);
    }

    @Test
    @DisplayName("Генерация и валидация токена")
    void shouldGenerateAndValidateToken() {
        String token = provider.generateToken("1", "USER");

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUserIdFromToken(token)).isEqualTo("1");
        assertThat(provider.getRoleFromToken(token)).isEqualTo("USER");
    }

    @Test
    @DisplayName("Разные пользователи — разные токены")
    void shouldGenerateDifferentTokens() {
        String token1 = provider.generateToken("1", "USER");
        String token2 = provider.generateToken("2", "ADMIN");

        assertThat(token1).isNotEqualTo(token2);
        assertThat(provider.getUserIdFromToken(token1)).isEqualTo("1");
        assertThat(provider.getUserIdFromToken(token2)).isEqualTo("2");
        assertThat(provider.getRoleFromToken(token2)).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Невалидный токен — исключение")
    void shouldThrowOnInvalidToken() {
        assertThatThrownBy(() -> provider.validateToken("invalid.token.here"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Пустой токен — исключение")
    void shouldThrowOnEmptyToken() {
        assertThatThrownBy(() -> provider.validateToken(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Истёкший токен — исключение")
    void shouldThrowOnExpiredToken() {
        JwtTokenProvider shortLived = new JwtTokenProvider(
                "test-secret-key-minimum-32-characters-long", -1000L); // уже истёк

        String token = shortLived.generateToken("1", "USER");

        assertThatThrownBy(() -> provider.validateToken(token))
                .isInstanceOf(JwtException.class);
    }
}