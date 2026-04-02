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
public class AdminRecentApplicationResponse {
    private Long candidateId;
    private String candidateName;
    private String courseName;
    private String status;
    private LocalDate appliedDate;
}
