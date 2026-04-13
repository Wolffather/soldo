package ru.savvy.soldo.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlugCheckResponse {
    private boolean available;
    private String suggested;
}
