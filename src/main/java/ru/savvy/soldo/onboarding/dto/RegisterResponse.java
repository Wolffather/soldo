package ru.savvy.soldo.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String token;
    private Long tenantId;
    private String tenantSlug;
    private String tenantName;
}
