package ru.savvy.soldo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private String role;
    private Long userId;
}