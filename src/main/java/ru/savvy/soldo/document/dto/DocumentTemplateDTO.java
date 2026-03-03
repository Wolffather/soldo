package ru.savvy.soldo.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentTemplateDTO {

    private Long id;

    @NotBlank(message = "Название обязательно")
    private String name;

    private String description;
    private String fileUrl;

    @NotBlank(message = "Формат категории обязателен")
    private String categoryFormat;

    private Boolean isRequired;
    private String createdAt;
}
