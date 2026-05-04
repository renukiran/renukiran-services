package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAttendanceStat {
    private Long batchId;
    private String batchName;
    private Integer averageAttendancePercentage;
    private Integer enrolledCount;
}
