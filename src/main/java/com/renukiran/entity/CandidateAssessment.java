package com.renukiran.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "candidate_assessment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_id", "batch_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private ApplicationForm candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(name = "mcq_score")
    private Integer mcqScore;

    @Column(name = "practical_score")
    private Integer practicalScore;

    @Column(name = "case_study_score")
    private Integer caseStudyScore;

    @Column(length = 1000)
    private String remarks;

    @Column(nullable = false)
    private Boolean published;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}
