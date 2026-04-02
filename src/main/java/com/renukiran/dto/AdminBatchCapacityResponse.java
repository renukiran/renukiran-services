package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBatchCapacityResponse {
    private Long batchId;
    private String courseName;
    private String batchName;
    private Integer enrolledCount;
    private Integer capacity;
    private Integer occupancyPercentage;
    private String batchStatus;
}
