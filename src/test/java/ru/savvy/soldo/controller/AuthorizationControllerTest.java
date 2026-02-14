package ru.savvy.soldo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.savvy.soldo.config.SecurityConfig;
import ru.savvy.soldo.dto.TelegramAuthRequest;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.security.JwtAuthenticationFilter;
import ru.savvy.soldo.security.JwtTokenProvider;
import ru.savvy.soldo.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorizationController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Успешная аутентификация через Telegram")
    void telegram_success() throws Exception {
        User user = User.builder()
                .id(1L).telegramId(123L).role("USER").build();

        when(userService.findOrCreateByTelegramId(any())).thenReturn(user);
        when(jwtTokenProvider.generateToken("1", "USER")).thenReturn("test-token");

        TelegramAuthRequest request = new TelegramAuthRequest();
        request.setTelegramId(123L);
        request.setFirstName("Test");

        mockMvc.perform(post("/auth/telegram")
                                .header("X-Bot-Secret", "1234") // из application-local.yml
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("Bearer test-token"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("Неверный секрет бота — 403")
    void telegram_wrongSecret_forbidden() throws Exception {
        TelegramAuthRequest request = new TelegramAuthRequest();
        request.setTelegramId(123L);
        request.setFirstName("Test");

        mockMvc.perform(post("/auth/telegram")
                                .header("X-Bot-Secret", "wrong-secret")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Без заголовка X-Bot-Secret — 400")
    void telegram_noHeader_badRequest() throws Exception {
        TelegramAuthRequest request = new TelegramAuthRequest();
        request.setTelegramId(123L);
        request.setFirstName("Test");

        mockMvc.perform(post("/auth/telegram")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Невалидный запрос — без telegramId — 400")
    void telegram_noTelegramId_badRequest() throws Exception {
        TelegramAuthRequest request = new TelegramAuthRequest();
        request.setFirstName("Test");
        // telegramId = null

        mockMvc.perform(post("/auth/telegram")
                                .header("X-Bot-Secret", "1234")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}