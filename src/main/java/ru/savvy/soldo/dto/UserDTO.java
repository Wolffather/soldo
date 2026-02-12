package ru.savvy.soldo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @Size(min = 1, message = "У пользователя должна быть хотя бы 1 роль")
    private List<String> roles;
}
