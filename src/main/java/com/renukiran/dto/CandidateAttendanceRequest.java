package com.renukiran.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CandidateAttendanceRequest {

    @NotNull(message = "Batch ID is mandatory")
    private Long batchId;

    @NotNull(message = "Attendance date is mandatory")
    private LocalDate attendanceDate;

    @Valid
    @NotEmpty(message = "Attendance entries are mandatory")
    private List<CandidateAttendanceEntryRequest> attendanceEntries;
}
