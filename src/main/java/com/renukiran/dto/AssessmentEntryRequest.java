package com.renukiran.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssessmentEntryRequest {

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    @NotNull(message = "MCQ score is required")
    @Min(value = 0, message = "MCQ score cannot be negative")
    @Max(value = 100, message = "MCQ score cannot exceed 100")
    private Integer mcqScore;

    @NotNull(message = "Practical score is required")
    @Min(value = 0, message = "Practical score cannot be negative")
    @Max(value = 100, message = "Practical score cannot exceed 100")
    private Integer practicalScore;

    @NotNull(message = "Case study score is required")
    @Min(value = 0, message = "Case study score cannot be negative")
    @Max(value = 100, message = "Case study score cannot exceed 100")
    private Integer caseStudyScore;

    private String remarks;
}
