package com.renukiran.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchResponse {

    private Long id;
    private String batchName;
    private Long courseId;
    private String timing;
    private String courseName;
    private Long trainerId;
    private String trainerName;
    private Integer capacity;
    private Long enrolledCount;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
