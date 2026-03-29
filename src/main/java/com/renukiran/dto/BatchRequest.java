package com.renukiran.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BatchRequest(
        @NotBlank(message = "Batch code is required")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Batch code must be alphanumeric")
        String batchCode,

        @NotBlank(message = "Course is required")
        String course,

        String trainer,
        String location,
        String dates,
        String startDate,
        String endDate,

        @NotNull(message = "Enrolled count is required")
        @Min(value = 0, message = "Enrolled must be >= 0")
        Integer enrolled,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer max,

        String status,
        String notes
) {}
