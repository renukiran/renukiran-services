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
public class AttendanceSaveResponse {
    private Long batchId;
    private LocalDate attendanceDate;
    private Integer markedCount;
    private Integer enrolledCount;
    private String message;
}
