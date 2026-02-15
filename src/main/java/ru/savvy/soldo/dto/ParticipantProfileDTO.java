package ru.savvy.soldo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ParticipantProfileDTO {
    private Long id;
    private Long userId;

    @NotBlank(message = "ФИО обязательно")
    private String fullName;

    private LocalDate birthDate;
    private String school;
    private String medicalNotes;

    @NotBlank(message = "ФИО родителя обязательно")
    private String parentFullName;

    @NotBlank(message = "Телефон родителя обязателен")
    private String parentPhone;

    private String parentEmail;
    private Boolean consentPersonalData;
    private Boolean consentPhotoVideo;
}