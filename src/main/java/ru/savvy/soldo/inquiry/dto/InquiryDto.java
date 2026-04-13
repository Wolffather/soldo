package ru.savvy.soldo.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDto {

    private Long id;

    @NotBlank(message = "Имя обязательно")
    private String name;

    private String phone;

    private String email;

    private String eventTitle;

    private String message;

    private LocalDateTime createdAt;
}
