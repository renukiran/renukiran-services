package com.renukiran.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;

    @Column(nullable = false, unique = true)
    private String batchCode;   // e.g. "B1", "B2"

    private String course;
    private String trainer;
    private String location;
    private String dates;       // human-readable "Jan – Mar 2026"
    private String startDate;
    private String endDate;

    @Column(nullable = false)
    @Builder.Default
    private Integer enrolled = 0;

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    private Integer max = 20;

    @Column(nullable = false)
    @Builder.Default
    private String status = "Upcoming";

    @Column(length = 500)
    private String notes;
}
