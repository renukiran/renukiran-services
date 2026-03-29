package com.renukiran.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PlacementRequest(
        @NotBlank(message = "Candidate name is required")
        String name,

        String employer,
        String role,

        @Min(value = 0, message = "Salary must be >= 0")
        Integer salary,

        String placedDate,
        String status,
        String course,
        String batch,
        String assessment
) {}
