package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcDashboardResponse {
    private String welcomeMessage;
    private LocalDate generatedOn;
    private OcDashboardMetricResponse newApplications;
    private OcDashboardMetricResponse underReview;
    private OcDashboardMetricResponse assignedToBatch;
    private OcDashboardMetricResponse pendingPlacement;
    private List<OcRecentApplicationResponse> recentApplications;
    private List<OcUpcomingFollowUpResponse> upcomingFollowUps;
}
