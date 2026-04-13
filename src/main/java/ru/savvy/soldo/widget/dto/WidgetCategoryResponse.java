package ru.savvy.soldo.widget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WidgetCategoryResponse {

    private Long id;
    private String name;
    private String iconUrl;
    private String format;
}
