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
public class AttendancePageResponse {
    private Long batchId;
    private String batchName;
    private String courseName;
    private LocalDate attendanceDate;
    private LocalDate previousDate;
    private LocalDate nextDate;
    private Integer classNumber;
    private Integer totalClasses;
    private Integer markedCount;
    private Integer enrolledCount;
    private List<AttendanceCandidateRowResponse> candidates;
}
