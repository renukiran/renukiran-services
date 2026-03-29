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
public class TrainerDashboardResponse {
    private Long trainerId;
    private String trainerName;
    private LocalDate generatedOn;
    private List<TrainerBatchCardResponse> activeBatches;
    private List<TrainerBatchLowAttendanceAlertResponse> lowAttendanceAlerts;
}
