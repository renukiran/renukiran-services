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
public class CandidateAttendanceSheetResponse {
    private Long batchId;
    private String batchName;
    private LocalDate attendanceDate;
    private boolean attendanceMarked;
    private List<CandidateAttendanceEntryResponse> candidates;
}
