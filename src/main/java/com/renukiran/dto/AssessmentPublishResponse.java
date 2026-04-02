package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentPublishResponse {
    private Long batchId;
    private Integer publishedCount;
    private Boolean published;
    private String message;
}
