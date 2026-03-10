package com.renukiran.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record TrainerAvailabilityRequest(
        @NotBlank(message = "Course code is required")
        String courseCode,
        @NotEmpty(message = "Time slot ids cannot be empty")
        List<UUID> timeSlotIds
) {}
