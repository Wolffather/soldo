package ru.savvy.soldo.config.dto;

import lombok.Data;

@Data
public class AppConfigUpdateRequest {
    private String name;
    private String domain;
    private String eventLabel;
    private String participantLabel;
    private String bookingLabel;
}
