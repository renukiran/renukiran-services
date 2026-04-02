package com.renukiran.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceSaveRequest {

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @Valid
    @NotEmpty(message = "Attendance entries cannot be empty")
    private List<AttendanceEntryRequest> entries;
}
