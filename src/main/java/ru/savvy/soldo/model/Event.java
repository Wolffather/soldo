package ru.savvy.soldo.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "type", length = 100, nullable = false)
    private String type;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "price", precision = 10)
    private Double price;

    @Column(name = "num_of_participants")
    private Integer numOfParticipants;

    @Column(name = "description", length = 1000)
    private String description;
}
