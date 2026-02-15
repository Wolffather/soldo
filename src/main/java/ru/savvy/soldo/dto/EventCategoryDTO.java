package ru.savvy.soldo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventCategoryDTO {
    private Long id;
    private String name;
    private String format;
    private String season;
    private String description;
    private String iconUrl;
}