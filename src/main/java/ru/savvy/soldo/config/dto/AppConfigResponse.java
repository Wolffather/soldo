package ru.savvy.soldo.config.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppConfigResponse {
    private String name;
    private String domain;
    private String eventLabel;
    private String participantLabel;
    private String bookingLabel;
}
