package com.renukiran.dto;

import com.renukiran.enums.CandidateAttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttendanceEntryRequest {

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    @NotNull(message = "Attendance status is required")
    private CandidateAttendanceStatus attendanceStatus;
}
