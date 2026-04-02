package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSaveResponse {
    private Long batchId;
    private Integer savedCount;
    private Integer passCount;
    private Integer failCount;
    private Double passRate;
    private String message;
}
