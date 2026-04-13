package ru.savvy.soldo.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotCategoryResponse {
    private Long id;
    private String name;
    private String format;
    private String iconUrl;
}
