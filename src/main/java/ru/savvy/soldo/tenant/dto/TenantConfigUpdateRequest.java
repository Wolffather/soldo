package ru.savvy.soldo.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TenantConfigUpdateRequest {

    @NotBlank
    private String eventLabel;

    @NotBlank
    private String participantLabel;

    @NotBlank
    private String bookingLabel;

    /** Название организации (отображается в интерфейсе) */
    private String name;

    /** Кастомный домен (только для PRO/ENTERPRISE) */
    private String domain;
}
