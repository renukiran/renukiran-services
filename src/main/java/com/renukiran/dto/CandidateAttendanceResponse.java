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
public class CandidateAttendanceResponse {
    private Long batchId;
    private String batchName;
    private LocalDate attendanceDate;
    private Integer totalCandidates;
    private Integer presentCount;
    private Integer absentCount;
    private String message;
}
