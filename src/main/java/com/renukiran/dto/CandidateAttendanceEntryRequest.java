package com.renukiran.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CandidateAttendanceEntryRequest {

    @NotNull(message = "Candidate ID is mandatory")
    private Long candidateId;

    @NotNull(message = "Attendance presence is mandatory")
    private Boolean present;
}
