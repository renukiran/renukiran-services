package com.renukiran.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseResponse {

    private Long id;
    private Long courseId;
    private String courseName;
    private Integer duration;
    private Integer maxBatchSize;
    private String instructor;
    private String category;
    private String description;
    private String mcqAssessment;
    private String practicalAssessment;
    private String caseStudyAssessment;
    private String status;
}