package ru.savvy.soldo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginRequest {

    @NotBlank(message = "Логин обязателен")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}