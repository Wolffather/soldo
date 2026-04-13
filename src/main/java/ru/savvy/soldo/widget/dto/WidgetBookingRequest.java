package ru.savvy.soldo.widget.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WidgetBookingRequest {

    @NotBlank
    private String tenantSlug;

    @NotNull
    private Long eventId;

    @NotBlank
    private String guestName;

    @NotBlank
    private String guestPhone;

    private String guestEmail;

    private String notes;
}
