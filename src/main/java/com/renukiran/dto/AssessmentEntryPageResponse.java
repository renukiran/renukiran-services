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
public class AssessmentEntryPageResponse {
    private Long batchId;
    private String batchName;
    private String courseName;
    private Integer mcqWeight;
    private Integer practicalWeight;
    private Integer caseStudyWeight;
    private Integer passThreshold;
    private Integer passCount;
    private Integer failCount;
    private Double passRate;
    private Boolean published;
    private List<AssessmentCandidateRowResponse> candidates;
}
