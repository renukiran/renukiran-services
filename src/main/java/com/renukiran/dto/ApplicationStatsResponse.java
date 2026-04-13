package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatsResponse {
    private long totalApplications;
    private long createdToday;
    private long draftCount;
    private long newCount;
    private long underReviewCount;
    private long selectedCount;
    private long assignedToBatchCount;
    private long trainingCount;
    private long pendingPlacementCount;
    private long placedCount;
    private long notPlacedCount;
}