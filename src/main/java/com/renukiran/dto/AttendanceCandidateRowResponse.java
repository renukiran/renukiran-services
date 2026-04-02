package com.renukiran.dto;

import com.renukiran.enums.CandidateAttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCandidateRowResponse {
    private Long candidateId;
    private Integer rowNumber;
    private String candidateName;
    private String mobileNumber;
    private Integer attendancePercentage;
    private CandidateAttendanceStatus todayStatus;
    private Integer streakDays;
    private String alertLabel;
}
