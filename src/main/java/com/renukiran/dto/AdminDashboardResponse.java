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
public class AdminDashboardResponse {
    private String welcomeMessage;
    private LocalDate generatedOn;
    private AdminDashboardMetricResponse totalCourses;
    private AdminDashboardMetricResponse activeBatches;
    private AdminDashboardMetricResponse candidatesEnrolled;
    private AdminDashboardMetricResponse pendingAssignments;
    private AdminDashboardMetricResponse assessmentPassRate;
    private AdminDashboardMetricResponse placementRate;
    private AdminDashboardMetricResponse jobRetention;
    private AdminDashboardMetricResponse averageAttendance;
    private List<AdminRecentApplicationResponse> recentApplications;
    private List<AdminBatchCapacityResponse> batchCapacityOverview;
    private List<AdminAttendanceAlertResponse> attendanceAlerts;
}
