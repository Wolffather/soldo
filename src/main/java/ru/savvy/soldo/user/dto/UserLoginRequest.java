package ru.savvy.soldo.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotBlank(message = "Логин обязателен")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}
