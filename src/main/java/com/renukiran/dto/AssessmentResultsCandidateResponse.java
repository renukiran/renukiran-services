package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResultsCandidateResponse {
    private Integer rowNumber;
    private String candidateName;
    private Integer mcqScore;
    private Integer practicalScore;
    private Integer caseStudyScore;
    private Double finalPercentage;
    private String result;
}
