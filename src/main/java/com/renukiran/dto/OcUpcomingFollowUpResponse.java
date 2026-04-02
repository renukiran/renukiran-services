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
public class OcUpcomingFollowUpResponse {
    private Long followUpId;
    private Long candidateId;
    private String candidateName;
    private String followUpType;
    private LocalDate dueDate;
    private String dueLabel;
}
