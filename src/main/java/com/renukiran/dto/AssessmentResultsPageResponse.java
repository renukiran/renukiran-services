package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResultsPageResponse {
    private Long batchId;
    private String batchName;
    private String courseName;
    private Integer totalAssessed;
    private Double passRate;
    private Double averageScore;
    private Double highestScore;
    private Integer mcqWeight;
    private Integer practicalWeight;
    private Integer caseStudyWeight;
    private Integer passThreshold;
    private Boolean published;
    private List<AssessmentResultsCandidateResponse> candidates;
}
