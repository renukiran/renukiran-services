package com.renukiran.entity;

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

    private String status;

    @OneToMany(mappedBy = "course")
    private List<Admission> admissions = new ArrayList<>();
}
