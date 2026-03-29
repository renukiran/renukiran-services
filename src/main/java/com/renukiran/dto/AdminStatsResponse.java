package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsResponse {
    // Operational Stats
    private long totalCourses;
    private long activeBatches;
    private long candidatesEnrolled;
    private long pendingAssignments;

    // Impact Stats
    private String assessmentPassRate;
    private String placementRate;
    private String jobRetention;
    private String avgAttendance;

    // Placement breakdown
    private long placedTotal;
    private long activeJobs;
    private long leftJob;
    private long unreadNotifications;
}
