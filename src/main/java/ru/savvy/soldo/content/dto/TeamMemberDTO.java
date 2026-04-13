package ru.savvy.soldo.content.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDTO {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String role;

    private String initials;
    private String bio;
    private String quote;
    private String photoUrl;
    private Integer sortOrder;
    private String createdAt;
}
