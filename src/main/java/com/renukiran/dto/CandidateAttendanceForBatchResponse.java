package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAttendanceForBatchResponse {
    private Long candidateId;
    private String candidateName;
    private Integer attendancePercentage;
    private List<DayStatusDto> dayStatuses; // in same order as days in AttendanceForBatchResponse
}
