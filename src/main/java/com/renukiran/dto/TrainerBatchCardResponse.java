package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerBatchCardResponse {
    private Long batchId;
    private String batchName;
    private String courseName;
    private Integer enrolledCandidatesCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer classProgressCompleted;
    private Integer classProgressTotal;
    private Integer classProgressPercentage;
    private Integer averageAttendancePercentage;
    private boolean attendanceMarkedToday;
    private Integer capacity;
}
