package com.renukiran.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(nullable = false)
    private String courseName;

    private Integer durationMonths;

    private Integer maxBatchSize;

    private String instructor;

    private String category;

    private String description;

    private String mcqAssessment;

    private String practicalAssessment;

    private String caseStudyAssessment;

    private Integer passThreshold;

    private Integer retentionRate;

    private String status;


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Batch> batchesList = new ArrayList<>();


}
