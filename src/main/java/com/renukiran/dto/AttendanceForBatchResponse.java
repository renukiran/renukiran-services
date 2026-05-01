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
public class AttendanceForBatchResponse {
    private Long batchId;
    private String batchName;
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<CandidateAttendanceForBatchResponse> candidateAttendances;
}
