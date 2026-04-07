package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAttendanceAlertResponse {
    private Long candidateId;
    private String candidateName;
    private Long batchId;
    private String courseName;
    private String batchName;
    private Integer attendancePercentage;
}
