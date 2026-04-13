package ru.savvy.soldo.onboarding.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String orgName;

    @NotBlank
    @Size(min = 2, max = 60)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Только строчные латинские буквы, цифры и дефис")
    private String slug;

    @NotBlank
    @Size(min = 2, max = 100)
    private String adminName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotNull
    private BusinessType businessType;
}
