package ru.savvy.soldo.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {
    private Long id;
    private String slug;
    private String name;
    private String domain;
    private String status;

    // Config labels
    private String eventLabel;
    private String participantLabel;
    private String bookingLabel;
}
