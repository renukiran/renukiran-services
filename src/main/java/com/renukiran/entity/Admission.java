package com.renukiran.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "admissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_admission_number", columnNames = "admissionNumber"),
                @UniqueConstraint(name = "uk_candidate_course", columnNames = {"candidate_id", "course_id"}
                ),
                @UniqueConstraint(name = "uk_candidate_batch_timing", columnNames = {"candidate_id", "batch_no", "timing"}
                )
        })
@Getter
@Setter
public class Admission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admissionId;

    @Column(nullable = false, unique = true)
    private String admissionNumber;

    private LocalDate admissionDate;

    private String batchNo;

    private String timing;

    // Relationships

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
