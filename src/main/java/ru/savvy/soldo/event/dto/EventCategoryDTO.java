package ru.savvy.soldo.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventCategoryDTO {
    private Long id;
    private String name;
    private String format;
    private String description;
    private String iconUrl;
}
