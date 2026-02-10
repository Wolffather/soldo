package ru.savvy.soldo.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {
    private String title;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;
    private Integer numOfParticipants;
    private String description;
}


